package org.fd.jcaex;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class of generic CAEX object.
 * This serves as a bridge between several versions of CAEX schema and their processing logic.
 * Using it, it is possible to implement several functions with only one implementation.
 * A GenericCAEXObject always contains the reference to its source CAEX object,
 * making it easy to switch back to work with the original object.
 */
public class GenericCAEXObject {

    private GenericCAEXObject parent;
    private String elementName;
    private final Object sourceCAEXObject;
    private final Map<String, Object> attributes;
    private final Map<String, Object> children;

    private GenericCAEXObject(Object caexObject, Map<String, Object> attributes, Map<String, Object> children)  {
        this.sourceCAEXObject = caexObject;
        this.attributes = Map.copyOf(attributes);

        children.forEach((key, val) -> {
            if (val instanceof GenericCAEXObject) {
                ((GenericCAEXObject) val).setParent(this);
            }

            if (val instanceof List) {
                ((List<?>) val).forEach(item -> {
                    if (item instanceof GenericCAEXObject) {
                        ((GenericCAEXObject) item).setParent(this);
                    }
                });
            }
        });

        this.children = Map.copyOf(children);
    }

    /**
     * Gets the original CAEX object of this generic object.
     *
     * @return Original CAEX object.
     */
    public Object getSourceCAEXObject() {
        return sourceCAEXObject;
    }

    /**
     * Gets attributes of this CAEX object.
     * These represent the real attributes of corresponding XML node.
     * Keys are attribute names as in XML.
     *
     * @return Map of attributes.
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Gets value of an attribute.
     *
     * @param attrName Name of attribute.
     * @return Value of attribute or null if attribute doesn't exist.
     */
    public Object getAttribute(String attrName) {
        return attributes.get(attrName);
    }

    /**
     * Gets child objects of this CAEX object.
     * Keys are element names as in XML.
     *
     * @return Map of child objects. Note: it's possible to have several children with the same element name.
     * So keys in this map are always element names, and values for such keys are lists.
     */
    public Map<String, Object> getChildren() {
        return children;
    }

    /**
     * Gets child objects based on given element name.
     *
     * @param elementName Name of child element (e.g. InternalElement).
     * @return Object of element (or null if no such child with the given name).
     */
    public Object getChildren(String elementName) {
        return children.get(elementName);
    }

    /**
     * Gets name of this element.
     *
     * @return Element name.
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * Returns parent object.
     *
     * @return Parent object.
     */
    public GenericCAEXObject getParent() {
        return parent;
    }

    /**
     * Sets parent object.
     *
     * @param parent PArent object.
     */
    private void setParent(GenericCAEXObject parent) {
        this.parent = parent;
    }

    /**
     * Sets element name.
     *
     * @param elementName Element name.
     */
    private void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Generates a GenericCAEXObject from a GeneralizableCAEXObject.
     *
     * @param caexObject Source CAEX object for generalization.
     * @return Generalized CAEX object.
     */
    public static GenericCAEXObject from(GeneralizableCAEXObject caexObject) {

        // Get fields
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(caexObject.getClass().getDeclaredFields()));

        // Get inherited fields
        Class<?> superClass = caexObject.getClass().getSuperclass();

        while (superClass != null && !superClass.isInstance(Object.class)) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }

        // Add attribute fields to the attributes map
        // and add other fields to children if they are generalizable
        Map<String, Object> attributes = new TreeMap<>();
        Map<String, Object> children = new TreeMap<>();
        fields.forEach(field -> {
            try {
                XmlAttribute xmlAttrAnnotation = field.getAnnotation(XmlAttribute.class);
                XmlElement xmlElementAnnotation = field.getAnnotation(XmlElement.class);

                // Attributes
                if (xmlAttrAnnotation != null) {
                    if (field.trySetAccessible()) {
                        Object attrVal = field.get(caexObject);
                        if (attrVal != null || xmlAttrAnnotation.required()) {
                            attributes.put(xmlAttrAnnotation.name(), attrVal);
                        }

                        field.setAccessible(false);
                    }
                } else if (xmlElementAnnotation != null) {
                    String elementName = xmlElementAnnotation.name();

                    // Children (if generalizable)
                    if (field.getGenericType() instanceof ParameterizedType) {

                        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
                        boolean isList = fieldType.getRawType().equals(List.class);

                        // Check if generic type of List implements GeneralizableCAEXObject interface
                        boolean isGeneralizable = isGeneralizable((Class<?>) fieldType.getActualTypeArguments()[0]);

                        if (isList && isGeneralizable) {
                            // That's a list
                            if (field.trySetAccessible()) {
                                try {
                                    List<?> sourceList = (List<?>) field.get(caexObject);

                                    if (sourceList != null) {
                                        List<GenericCAEXObject> childList = new ArrayList<>();

                                        sourceList.forEach(listItem -> {
                                            GenericCAEXObject obj = ((GeneralizableCAEXObject) listItem).generalize();
                                            obj.setElementName(elementName);
                                            childList.add(obj);
                                        });

                                        children.put(xmlElementAnnotation.name(), childList);
                                    }
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                                field.setAccessible(false);
                            }
                        }
                    } else if (isGeneralizable(field.getType())) {
                        // It's a single generalizable object
                        if (field.trySetAccessible()) {
                            try {
                                GeneralizableCAEXObject fieldVal = ((GeneralizableCAEXObject) field.get(caexObject));

                                if (fieldVal != null) {
                                    GenericCAEXObject obj = fieldVal.generalize();
                                    obj.setElementName(elementName);
                                    children.put(xmlElementAnnotation.name(), obj);
                                }
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            field.setAccessible(false);
                        }
                    } else {
                        // It's a simple object
                        if (field.trySetAccessible()) {
                            try {
                                Object fieldVal = field.get(caexObject);
                                if (fieldVal != null || xmlElementAnnotation.required()) {
                                    children.put(xmlElementAnnotation.name(), field.get(caexObject));
                                }
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            field.setAccessible(false);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        return new GenericCAEXObject(caexObject, attributes, children);
    }

    /**
     * Checks if given object is a GeneralizableCAEXObject.
     *
     * @param clazz Object to test.
     * @return True if the specified object implements the GeneralizableCAEXObject interface.
     */
    private static boolean isGeneralizable(Class<?> clazz) {

        boolean res = false;
        Class<?> c = (Class<?>) clazz.getGenericSuperclass();

        while (c != null && !res) {
            res = Arrays.asList(c.getInterfaces()).contains(GeneralizableCAEXObject.class);

            c = (Class<?>) c.getGenericSuperclass();
        }

        return res;
    }

    @Override
    public String toString() {
        return "GenericCAEXObject{" +
                "elementName='" + elementName + '\'' +
                ", attributes=" + attributes.keySet().stream().map(
                        key -> "[" + key + "='" + attributes.get(key) + "']").collect(Collectors.joining()) +
                '}';
    }
}

package org.fd.jcaex.filter;

import org.fd.jcaex.GenericCAEXObject;

import java.util.*;

/**
 * Class of an element filter.
 * <p>
 * Can be used for filtering nodes w/ or w/o attributes.
 * Filters for descendant objects can also be set.
 * </p>
 */
public class ElementFilter extends AbstractElementFilter {
    String elementName;
    Set<String> attributes = new HashSet<>();

    ElementFilter(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Sets attribute filters on this element filter.
     *
     * @param attributes Multiple String values can be set. For example <i>.withAttr("attrA", "attrB='value'")</i> will
     *                   filter elements having <i>attrA</i> (don't care about its value) and also having <i>attrB</i>
     *                   with value <i>value</i>.
     * @return This ElementFilter for further operations.
     */
    public ElementFilter withAttr(String... attributes) {

        this.attributes.addAll(Arrays.asList(attributes));

        return this;
    }

    /**
     * Constructs filter string for CAEXFilter.
     *
     * @return Filter string.
     * @see org.fd.jcaex.CAEXFile
     *
     */
    String getFilterString() {
        return elementName +
                attributes.stream().reduce("", (s1, s2) -> s1 + ("[" + s2 + "]"));
    }

    @Override
    List<GenericCAEXObject> execute(GenericCAEXObject object) {
        return CAEXFilter.applyFilter(object, getFilterString(), parentFilterNode.isDeep());
    }

    List<GenericCAEXObject> execute(List<GenericCAEXObject> objects) {
        return CAEXFilter.applyFilter(objects, getFilterString(), parentFilterNode.isDeep());
    }

    /**
     * Creates a filter node for filtering first level children.
     *
     * @return Newly created filter node.
     */
    public FilterNode havingChild() {
        return parentFilterNode.registerChild(new FilterNode(false, this.parentFilterNode));
    }

    /**
     * Creates a filter node for filtering all children down to the object hierarchy.
     *
     * @return Newly created filter node.
     */
    public FilterNode havingAll() {
        return parentFilterNode.registerChild(new FilterNode(true, this.parentFilterNode));
    }

    /**
     * Creates a new element filter for given element name.
     *
     * @param elementName Name of element.
     * @return Newly created element filter.
     */
    public static ElementFilter _for(String elementName) { return new ElementFilter(elementName); }

    /**
     * Convenience method for creating a new element filter for any element.
     * Same as .name("").
     *
     * @return Newly created element filter.
     */
    public static ElementFilter any() {
        return new ElementFilter("");
    }


}

package org.fd.jcaex.filter;

import org.fd.jcaex.GeneralizableCAEXObject;
import org.fd.jcaex.GenericCAEXObject;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class of CAEX object filter.
 * <p>
 * It's a class used for filtering specific CAEX objects.
 * </p>
 */
public class CAEXFilter {

    GenericCAEXObject origin;
    List<FilterNode> filterNodes;

    private CAEXFilter(GeneralizableCAEXObject caexObject) {
        this.origin = caexObject.generalize();
    }

    private CAEXFilter(GenericCAEXObject caexObject) {
        this.origin = caexObject;
    }

    /**
     * Creates a filter for all descendant objects of the origin.
     *
     * @return Newly created filter node.
     */
    public FilterNode all() {

        if (filterNodes == null) {
            filterNodes = new ArrayList<>();
        }

        FilterNode res = new FilterNode(true);
        res.setParentFilter(this);
        filterNodes.add(res);

        return res;
    }

    /**
     * Creates a filter for child objects of the origin.
     *
     * @return Newly created filter node.
     */
    public FilterNode children() {

        if (filterNodes == null) {
            filterNodes = new ArrayList<>();
        }

        FilterNode res = new FilterNode(false);
        res.setParentFilter(this);
        filterNodes.add(res);

        return res;
    }

    /**
     * Clears all filter nodes from this filter.
     * This method can be used to reuse filter objects, making it unnecessary to generalize the source object
     * again, thus preserving resources.
     *
     * @return Object of this filter.
     */
    public CAEXFilter clear() {

        this.filterNodes.clear();

        return this;
    }

    /**
     * Executes the constructed filter on the origin object.
     *
     * @return List of filter results or an empty list if no results.
     */
    public List<GenericCAEXObject> execute() {

        if (filterNodes != null) {
            List<GenericCAEXObject> res = new ArrayList<>();

            for (FilterNode fp : filterNodes) {
                res.addAll(fp.execute(origin));
            }

            return res;
        }

        return List.of();
    }

    /**
     * Executes the constructed filter on the origin object and maps the result.
     * Convenience method for stream().map(func).collect(Collectors.toList()).
     *
     * @param mappingFunction Mapping function.
     * @return Resulting list.
     */
    public List<Object> executeAndMap(Function<GenericCAEXObject, Object> mappingFunction) {
        return execute().stream().map(mappingFunction).collect(Collectors.toList());
    }

    /**
     * Filter elements using specified filter parameters.
     *
     * @param rootObject Object on which the filter will be applied.
     * @param filterCriteria String which defines the filter criteria in 'kind of' simplified XPath format.<br>
     *                       For example to filter elements by tag name:<br><br>
     *                       InternalElement<br><br>
     *                       To filter all elements having a specific attribute:<br><br>
     *                       [attribute]<br><br>
     *                       To filter all elements having specific attribute values:<br><br>
     *                       [attribute1='value1'][attribute2='value2']<br><br>
     *                       If multiple attribute values are set then they are treated as an AND function during evaluation.<br><br>
     *                       All of the above can be combined but the criteria must always start with element name if
     *                       element name based filtering is needed.
     *                       Multiple criteria could be set using a dot as separator and these are treated as an OR function on each object.
     *                       So the returning list will contain elements which fulfills any criteria of the given criteria list.
     * @param deepSearch     If set to true, children will be tested all the way down the tree, otherwise only the root object will be tested.
     * @return List containing elements which fulfills criteria.
     */
    public static List<GenericCAEXObject> applyFilter(GenericCAEXObject rootObject, String filterCriteria, boolean deepSearch) {

        List<GenericCAEXObject> res = new ArrayList<>();

        String[] criteriaList = filterCriteria.split(",");

        Pattern p = Pattern.compile("\\s*(?:((?<elementName>\\w+)\\s*$)|((?<elemName>\\w+)?\\s*(?:\\[\\s*(?<attrName>\\w+)\\s*(?:\\s*(?:='(?<attrVal>.*?)'\\s*)?)?\\])+?))");

        Arrays.asList(criteriaList).forEach(criteria -> {
            Matcher m = p.matcher(criteria);

            if (m.matches()) {

                boolean elementNameFilterUsed = m.group("elementName") != null || m.group("elemName") != null;
                boolean attributeFilterUsed = m.group("attrName") != null;

                if (elementNameFilterUsed) {
                    String elementName = m.group("elementName") != null ? m.group("elementName") : m.group("elemName");

                    Object child = rootObject.getChildren().get(elementName);

                    if (child != null) {
                        if (child instanceof GenericCAEXObject) {
                            res.add((GenericCAEXObject) child);
                        }

                        if (child instanceof List) {
                            ((List<?>) child).forEach(item -> {
                                if (item instanceof GenericCAEXObject) {
                                    res.add((GenericCAEXObject) item);
                                }

                            });
                        }
                    }

                    if (deepSearch) {
                        rootObject.getChildren().forEach((key, val) -> {
                            if (val instanceof GenericCAEXObject) {
                                res.addAll(applyFilter((GenericCAEXObject) val, filterCriteria, deepSearch));
                            }

                            if (val instanceof List) {
                                ((List<?>) val).forEach(item -> {
                                    if (item instanceof GenericCAEXObject) {
                                        res.addAll(applyFilter((GenericCAEXObject) item, filterCriteria, deepSearch));
                                    }

                                });
                            }
                        });
                    }
                } else {
                    rootObject.getChildren().forEach((key, val) -> {
                        if (val instanceof GenericCAEXObject) {
                            res.add((GenericCAEXObject) val);
                        }

                        if (val instanceof List<?>) {
                            res.addAll((Collection<? extends GenericCAEXObject>) val);
                        }
                    });
                }

                // Now check attributes
                if (attributeFilterUsed) {
                    Iterator<GenericCAEXObject> iter = res.iterator();

                    while (iter.hasNext()) {
                        GenericCAEXObject obj = iter.next();

                        m.reset();

                        while (m.find()) {
                            String attrName = m.group("attrName");
                            String attrVal = m.group("attrVal");

                            boolean fits = obj.getAttributes().containsKey(attrName) && (m.group("attrVal") == null || ((String) obj.getAttribute(attrName)).matches(attrVal));

                            if (!fits) {
                                iter.remove();
                                break;
                            }
                        }
                    }
                }
            }
        });

        return res;
    }

    /**
     * Applies filter on a collection of generic CAEX objects.
     *
     * @param objects        List of objects.
     * @param filterCriteria Filter criteria as string.
     * @param deepSearch     Whether this filter should be applied on direct children (false) or all descendants (true).
     * @return List of objects matching the criteria or empty list if no results.
     */
    public static List<GenericCAEXObject> applyFilter(List<GenericCAEXObject> objects, String filterCriteria, boolean deepSearch) {
        List<GenericCAEXObject> res = new ArrayList<>();

        if (objects != null) {
            objects.forEach(object -> res.addAll(applyFilter(object, filterCriteria, deepSearch)));
        }

        return res;
    }

    /**
     * Creates a filter for a given CAEX object.
     *
     * @param caexObject Origin object of filter operations.
     * @return Newly created CAEXFilter object.
     */
    public static CAEXFilter forObject(GeneralizableCAEXObject caexObject) {
        return new CAEXFilter(caexObject);
    }

    /**
     * Creates a filter for a given CAEX object.
     *
     * @param caexObject Origin object of filter operations.
     * @return Newly created CAEXFilter object.
     */
    public static CAEXFilter forObject(GenericCAEXObject caexObject) {
        return new CAEXFilter(caexObject);
    }
}

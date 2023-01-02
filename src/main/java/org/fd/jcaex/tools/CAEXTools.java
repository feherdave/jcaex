package org.fd.jcaex.tools;

import org.fd.jcaex.GenericCAEXObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A common tools class for CAEXFile operations.
 */
public class CAEXTools {

    /**
     * Filter elements along specified filter parameters.
     *
     * @param filterCriteria String which defines the filter criteria in 'kind of' simplified XPath format.<br/>
     *                    For example to filter elements by tag name:<br/><br/>
     *                      InternalElement<br/><br/>
     *                    To filter all elements having a specific attribute:<br/><br/>
     *                      [attribute]<br/><br/>
     *                    To filter all elements having specific attribute values:<br/><br/>
     *                      [attribute1="value1"][attribute2="value2"]<br/><br/>
     *                    If multiple attribute values are set then they are treated as an AND function during evaluation.<br/><br/>
     *                    All of the above can be combined but the criteria must always start with element name if
     *                    element name based filtering is needed.
     *                    Multiple criteria could be set using a dot as separator and these are treated as an OR function on each object.
     *                    So the returning list will contain elements which fulfills any criteria of the given criteria list.
     * @param deepSearch If set to true, children will be tested all the way down the tree, otherwise only the root object will be tested.
     * @return List<GenericCAEXObject> List containing elements which fulfills criteria.
     */
    public static List<GenericCAEXObject> applyFilter(GenericCAEXObject rootObject, String filterCriteria, boolean deepSearch) {

        List<GenericCAEXObject> res = new ArrayList<>();

        String[] criteriaList = filterCriteria.split(",");

        Pattern p = Pattern.compile("\\s*(?:((?<elementName>\\w+)\\s*$)|((?<elemName>\\w+)?\\s*(?:\\[\\s*(?<attrName>\\w+)\\s*(?:\\s*(?:=\"(?<attrVal>.*?)\"\\s*)?)?\\])+?))");

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

                           boolean fits = obj.getAttributes().containsKey(attrName) && (m.group("attrVal") == null || obj.getAttribute(attrName).equals(attrVal));

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
     * @param objects
     * @param filterCriteria
     * @param deepSearch
     * @return
     */
    public static List<GenericCAEXObject> applyFilter(List<GenericCAEXObject> objects, String filterCriteria, boolean deepSearch) {
        List<GenericCAEXObject> res = new ArrayList<>();

        if (objects != null) {
            objects.forEach(object -> res.addAll(applyFilter(object, filterCriteria, deepSearch)));
        }

        return res;
    }

}

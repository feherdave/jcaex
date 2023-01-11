package org.fd.jcaex.filter;

import org.fd.jcaex.GenericCAEXObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for defining a filter node.
 * It's called a node because the final filtering structure looks like a tree.
 * This is required because of the tree-like structure of CAEX objects.
 */
public class FilterNode {

    boolean deepSearch;
    CAEXFilter parentFilter;
    FilterNode parentFilterNode;
    AbstractElementFilter elementFilter;
    List<FilterNode> childFilterNodes;
    List<String> childFilterOperators = new ArrayList<>();

    FilterNode(boolean deepSearch) {
        this.deepSearch = deepSearch;
    }

    FilterNode(boolean deepSearch, FilterNode parentFilterNode) {
        this.deepSearch = deepSearch;
        this.parentFilterNode = parentFilterNode;
    }

    /**
     * Creates a child filter node which will be tested using AND logic against previous filter nodes.
     * For example: If filter node <b>A</b> is defined then using <i>and()</i> for defining filter node <b>B</b>
     * means that only objects which exist in both filter A AND filter B will be returned.
     * All other previously filtered objects will be removed from results.
     *
     * @return Newly created FilterNode.
     */
    public FilterNode and() {
        FilterNode fn = parentFilterNode != null ? parentFilterNode : this;

        fn.childFilterOperators.add("A");

        return fn.registerChild(new FilterNode(this.deepSearch, this));
    }

    /**
     * Finishes definition of this filter node and returns the parent filter node for further definition.
     *
     * @return Parent filter node.
     */
    public FilterNode done() {
        return parentFilterNode;
    }

    /**
     * Creates a child filter node which will be tested using OR logic against previous filter nodes.
     * For example: If we have defined filter node <b>A</b> then using <i>or()</i> for defining filter node <b>B</b>
     * means that result objects of filter B will be added to results.
     *
     * @return Newly created FilterNode.
     */
    public FilterNode or() {
        FilterNode fn = parentFilterNode != null ? parentFilterNode : this;

        fn.childFilterOperators.add("O");

        return fn.registerChild(new FilterNode(this.deepSearch, this));
    }

    /**
     * Sets an 'any' element filter on this node.
     *
     * @return ElementFilter object.
     */
    public ElementFilter anyElement() {

        ElementFilter ef = ElementFilter.any();
        ef.setParentFilterNode(this);
        this.elementFilter = ef;

        return (ElementFilter) elementFilter;
    }

    /**
     * Sets an element filter on this node.
     *
     * @param elementFilter {@link ElementFilter} object defining filter criteria.
     * @return ElementFilter object.
     */
    public ElementFilter element(ElementFilter elementFilter) {

        elementFilter.setParentFilterNode(this);
        this.elementFilter = elementFilter;

        return elementFilter;
    }

    /**
     * Sets an element filter on this node without specifying attribute filters.
     *
     * @param elementName Name of element.
     * @return ElementFilter object.
     */
    public ElementFilter element(String elementName) {
        ElementFilter ef = ElementFilter._for(elementName);
        ef.setParentFilterNode(this);
        this.elementFilter = ef;

        return (ElementFilter) elementFilter;
    }

    /**
     * Sets an element filter on this node specifying attribute filters.
     *
     * @param elementName Name of element.
     * @param attributes Attribute filters.
     * @return ElementFilter object.
     */
    public ElementFilter element(String elementName, String... attributes) {
        ElementFilter ef = ElementFilter._for(elementName).withAttr(attributes);
        ef.setParentFilterNode(this);
        this.elementFilter = ef;

        return (ElementFilter) elementFilter;
    }

    /**
     * Finishes setting filter properties and executes the whole filter chain.
     *
     * @return List of filter results or an empty list if no results or no parent is set.
     */
    public List<GenericCAEXObject> execute() {
        return parentFilter == null ? List.of() : parentFilter.execute();
    }

    /**
     * Executes the filter on the given object.
     *
     * @param object Origin object where filtering starts.
     * @return List of generic CAEX objects or empty list if no results.
     */
    List<GenericCAEXObject> execute(GenericCAEXObject object) {
        List<GenericCAEXObject> res = new ArrayList<>();

        List<GenericCAEXObject> parentRes = elementFilter.execute(object);

        if (childFilterNodes == null) {
            return parentRes; //.stream().map(GenericCAEXObject::getParent).collect(Collectors.toList());
        }

        for (int i = 0; i < childFilterNodes.size(); i++) {

            String op;

            if (i > 0) {
                op = childFilterOperators.get(i - 1);
            } else {
                op = "O";
            }

            List<GenericCAEXObject> actualRes = childFilterNodes.get(i).execute(parentRes);

            switch (op) {
                case "O":
                    res.addAll(actualRes);
                    break;

                case "A":
                    List<GenericCAEXObject> actualResParents = actualRes.stream().map(GenericCAEXObject::getParent).collect(Collectors.toList());
                    List<GenericCAEXObject> resParents = res.stream().map(GenericCAEXObject::getParent).collect(Collectors.toList());

                    if (actualResParents.size() < resParents.size()) {
                        actualResParents.retainAll(resParents);

                        res = actualRes.stream().filter(p -> actualResParents.contains(p.getParent())).collect(Collectors.toList());
                    } else {
                        resParents.retainAll(actualResParents);

                        res = res.stream().filter(p -> resParents.contains(p.getParent())).collect(Collectors.toList());
                    }

                    break;

                default:

            }
        }

        return parentFilterNode != null ? res.stream().map(GenericCAEXObject::getParent).collect(Collectors.toList()) : res;
    }

    /**
     * Executes the filter on the given list of objects.
     *
     * @param objects Origin object where filtering starts.
     * @return List of generic CAEX objects or emtpy list if no results.
     */
    List<GenericCAEXObject> execute(List<GenericCAEXObject> objects) {
        List<GenericCAEXObject> res = new ArrayList<>();

        List<GenericCAEXObject> parentRes = elementFilter.execute(objects);

        if (childFilterNodes == null) {
            return parentRes; //.stream().map(GenericCAEXObject::getParent).collect(Collectors.toList());
        }

        for (int i = 0; i < childFilterNodes.size(); i++) {

            String op;

            if (i > 0) {
                op = childFilterOperators.get(i - 1);
            } else {
                op = "O";
            }

            List<GenericCAEXObject> actualRes = childFilterNodes.get(i).execute(parentRes);

            switch (op) {
                case "O":
                    res.addAll(actualRes);
                    break;

                case "A":
                    res = res.stream().filter(actualRes::contains).collect(Collectors.toList());
                    break;

                default:

            }
        }

        return parentFilterNode != null ? res.stream().map(GenericCAEXObject::getParent).collect(Collectors.toList()) : res;
    }

    CAEXFilter getParentFilter() {
        return parentFilter;
    }

    /**
     * Gets filter objects of this node.
     *
     * @return Filter object of this node.
     */
    AbstractElementFilter getElementFilter() {
        return elementFilter;
    }

    /**
     * Returns if deep filtering is set up.
     *
     * @return True if yes, otherwise false.
     */
    boolean isDeep() {
        return deepSearch;
    }

    /**
     * Adds a filter which will be executed on child nodes.
     *
     * @param child Child filter node to be registered on this filter node.
     * @return This filter node.
     */
    FilterNode registerChild(FilterNode child) {
        if (childFilterNodes == null) {
            childFilterNodes = new ArrayList<>();
        }

        child.setParentFilter(this.parentFilter);
        childFilterNodes.add(child);

        return child;
    }

    /**
     * Sets parent filter of this element filter.
     *
     * @param parentFilter Parent CAEXFilter object.
     */
    void setParentFilter(CAEXFilter parentFilter) {
        this.parentFilter = parentFilter;
    }

    /**
     * Sets a text node filter on this node.
     *
     * @param nodeName Name of node.
     * @param nodeValue Text value of node.
     * @return Newly created text node filter object.
     */
    public FilterNode textNode(String nodeName, String nodeValue) {

        this.elementFilter = new TextNodeFilter(this, nodeName, nodeValue);

        return this;
    }

    /**
     * Sets a text node filter on this node.
     *
     * @param textNodeFilter TextNodeFilter object.
     * @return Text node filter object.
     */
    public FilterNode textNode(TextNodeFilter textNodeFilter) {

        textNodeFilter.setParentFilterNode(this);
        this.elementFilter = textNodeFilter;

        return this;
    }
}

package org.fd.jcaex.filter;

import org.fd.jcaex.GenericCAEXObject;

import java.util.List;

/**
 * Abstract class of element filters.
 */
public class AbstractElementFilter {

    protected FilterNode parentFilterNode = null;

    /**
     * Executes the whole filter chain.
     *
     * @return List of objects which fulfills the filter criteria or empty list if no results.
     */
    public List<GenericCAEXObject> execute() {
        return parentFilterNode.getParentFilter() != null ? parentFilterNode.getParentFilter().execute() : List.of();
    }

    /**
     * Executes this filter on the given object.
     *
     * @param object Object on which the filter operation will be done.
     * @return List of objects which fulfills the filter criteria or empty list if no results.
     */
    List<GenericCAEXObject> execute(GenericCAEXObject object) {
        return List.of();
    }

    /**
     * Executes this filter on the given list of objects.
     *
     * @param objects List of objects on which the filter operation will be done.
     * @return List of objects which fulfills the filter criteria or empty list if no results.
     */
    List<GenericCAEXObject> execute(List<GenericCAEXObject> objects) {
        return List.of();
    }

    /**
     * Creates a sibling filter node which will be tested using AND logic against previous filter nodes.
     * For example: If filter node <b>A</b> is defined then using <i>and()</i> for defining filter node <b>B</b>
     * means that result objects which exist in A AND B filters also will be returned.
     * All other objects will be removed from results.
     *
     * @return Newly created FilterNode.
     */
    public FilterNode and() {
        return parentFilterNode.and();
    }

    /**
     * Creates a child filter node which will be tested using OR logic against previous filter nodes.
     * For example: If we have defined filter node <b>A</b> then using <i>or()</i> for defining filter node <b>B</b>
     * means that result objects of filter B will be added to results.
     *
     * @return Newly created FilterNode.
     */
    public FilterNode or() {
        return parentFilterNode.or();
    }

    /**
     * Finishes filter definition on its level and returns to the parent filter node for further definition.
     *
     * @return Parent filter node.
     */
    public FilterNode done() { return parentFilterNode; }

    /**
     * Returns parent element filter.
     *
     * @return Parent ElementFilter.
     */
    public AbstractElementFilter parent() {
        return parentFilterNode.getElementFilter();
    }

    /**
     * Sets parent filter node for this filter.
     *
     * @param parentFilterNode PArent filter node object.
     * @throws CAEXFilterException if this filter is already assigned to a filter node.
     */
    public void setParentFilterNode(FilterNode parentFilterNode) {
        if (this.parentFilterNode == null) {
            this.parentFilterNode = parentFilterNode;
        } else {
            throw new CAEXFilterException("Filter '" + this + "' is already assigned to filter node '" + parentFilterNode);
        }
    }
}
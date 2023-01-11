package org.fd.jcaex.filter;

import org.fd.jcaex.GenericCAEXObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class of a text node filter.
 * <p>
 * Can be used for filtering nodes containing a single text value.
 *</p>
 */
public class TextNodeFilter extends AbstractElementFilter {
    String nodeName;
    String nodeValue;

    TextNodeFilter(String nodeName, String nodeValue) {
        this.nodeName = nodeName;
        this.nodeValue = nodeValue;
    }

    TextNodeFilter(FilterNode parentFilterNode, String nodeName, String nodeValue) {
        this.parentFilterNode = parentFilterNode;
        this.nodeName = nodeName;
        this.nodeValue = nodeValue;
    }

    List<GenericCAEXObject> execute(GenericCAEXObject origin) {
        Map<String, Object> children = origin.getChildren();

        if (children.size() != 1 ||
                !nodeValue.equals(children.get(nodeName))) {
            return List.of();
        }

        return List.of(origin);
    }

    List<GenericCAEXObject> execute(List<GenericCAEXObject> objects) {

        List<GenericCAEXObject> res = new ArrayList<>();

        for (GenericCAEXObject obj : objects) {
            res.addAll(execute(obj));
        }

        return res;
    }

    @Override
    public String toString() {
        return "TextNodeFilter{" +
                "nodeName='" + nodeName + '\'' +
                ", nodeValue='" + nodeValue + '\'' +
                '}';
    }

    /**
     * Creates a new text node filter.
     *
     * @param nodeName Node name.
     * @param nodeValue Text contained in the node.
     * @return Newly created text node filter.
     */
    public static TextNodeFilter _for(String nodeName, String nodeValue) {
        return new TextNodeFilter(nodeName, nodeValue);
    }

    /**
     * Sets node name and value at once for this filter.
     *
     * @param nodeName Node name.
     * @param nodeValue Node value.
     * @return This filter object.
     */
    public TextNodeFilter set(String nodeName, String nodeValue) {
        this.nodeName = nodeName;
        this.nodeValue = nodeValue;

        return this;
    }

    /**
     * Sets node name for this filter.
     *
     * @param nodeName Node name.
     * @return This filter object.
     */
    public TextNodeFilter setNodeName(String nodeName) {
        this.nodeName = nodeName;

        return this;
    }

    /**
     * Sets node name for this filter.
     *
     * @param nodeValue Node value.
     * @return This filter object.
     */
    public TextNodeFilter setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;

        return this;
    }
}

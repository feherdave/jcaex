package org.fd.jcaex;

public interface GeneralizableCAEXObject {

    default GenericCAEXObject generalize() {
        return GenericCAEXObject.from(this);
    }
}

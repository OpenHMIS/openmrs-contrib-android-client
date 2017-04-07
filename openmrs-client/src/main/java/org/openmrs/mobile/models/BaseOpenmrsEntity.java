package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class BaseOpenmrsEntity extends BaseOpenmrsObject implements Serializable {
    @Expose
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}

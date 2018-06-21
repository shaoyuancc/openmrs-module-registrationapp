package org.openmrs.module.registrationapp.metadata.bundles;

import org.openmrs.module.registrationapp.metadata.HaitiPatientIdentifierTypes;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

@Component
public class HaitiPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing Haiti PatientIdentifierTypes");

        install(HaitiPatientIdentifierTypes.NIF_ID);
        install(HaitiPatientIdentifierTypes.CIN_ID);
        install(HaitiPatientIdentifierTypes.BIOMETRIC_REF_NUMBER);
    }

}

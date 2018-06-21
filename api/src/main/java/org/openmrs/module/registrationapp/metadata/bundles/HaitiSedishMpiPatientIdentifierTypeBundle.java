package org.openmrs.module.registrationapp.metadata.bundles;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.registrationapp.metadata.HaitiPatientIdentifierTypes;
import org.springframework.stereotype.Component;

@Component
public class HaitiSedishMpiPatientIdentifierTypeBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {

        log.info("Installing SEDISH MPI PatientIdentifierType");

        install(HaitiPatientIdentifierTypes.SEDISH_MPI_ECID);
    }

}

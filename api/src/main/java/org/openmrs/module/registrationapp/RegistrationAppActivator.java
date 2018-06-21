/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.registrationapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;

// CCSY ADDED
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.api.PatientService;
import org.openmrs.GlobalProperty;
import org.openmrs.module.registrationapp.metadata.bundles.HaitiPatientIdentifierTypeBundle;
import org.openmrs.module.registrationapp.metadata.bundles.HaitiSedishMpiPatientIdentifierTypeBundle;
import org.openmrs.module.registrationapp.metadata.bundles.RegistrationAppMetadataBundle;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class RegistrationAppActivator extends BaseModuleActivator {

    private static final Log log = LogFactory.getLog(RegistrationAppActivator.class);

    /**
     * @see ModuleActivator#willRefreshContext()
     */
    public void willRefreshContext() {
        log.info("Refreshing Registration App Patient Dashboard Module");
    }

    /**
     * @see ModuleActivator#contextRefreshed()
     */
    public void contextRefreshed() {
        log.info("Registration App Patient Dashboard Module refreshed");
    }

    /**
     * @see ModuleActivator#willStart()
     */
    public void willStart() {
        log.info("Starting Registration App Patient Dashboard Module");
    }

    /**
     * @see ModuleActivator#started()
     */
    public void started() {

        setupIdentifierTypeGlobalProperties(Context.getAdministrationService(), Context.getService(IdentifierSourceService.class));

        super.started();
    }

    private void setupIdentifierTypeGlobalProperties(AdministrationService administrationService, IdentifierSourceService identifierSourceService) {
        // set RegistrationCoreConstants.GP_IDENTIFIER_SOURCE_ID based off the autogeneration options of the primary
        // identifier type from the EMR API module, if possible. (This may not be possible if things aren't configured right,
        // e.g. due to module startup order, in which case we log a warning and continue.)
        try {
            AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
            PatientService patientService = Context.getPatientService();

            EmrApiProperties emrApiProperties = Context.getRegisteredComponents(EmrApiProperties.class).iterator().next();
            PatientIdentifierType primaryIdentifierType = emrApiProperties.getPrimaryIdentifierType();

            // IdentifierSource sourceForPrimaryType = identifierSourceService.getAutoGenerationOption(primaryIdentifierType).getSource();

            // administrationService.setGlobalProperty(RegistrationCoreConstants.GP_OPENMRS_IDENTIFIER_SOURCE_ID, sourceForPrimaryType.getId().toString());

            // Setup for MPI Sedish Demo

            log.info("Updating OpenMRS ID to iSantePlus ID");
            changeOpenmrsIdName(patientService);

            log.info("Installing Metadata Bundles");
            installMetadataBundles();

            //Disable the following apps
            appFrameworkService.disableApp("referenceapplication.registrationapp.registerPatient");

        }
        catch (Exception ex) {
            log.warn("Failed to set global property for " + RegistrationCoreConstants.GP_OPENMRS_IDENTIFIER_SOURCE_ID + " based on " + EmrApiConstants.PRIMARY_IDENTIFIER_TYPE + ". Will try again at next module startup.", ex);
        }
    }


    /**
     * Change the OpenMRD Id name to iSantePlus ID if it has already been loaded.
     * @param patientService
     */
    public void changeOpenmrsIdName(PatientService patientService) throws Exception{

        PatientIdentifierType openmrsIdType = patientService.getPatientIdentifierTypeByName("OpenMRS ID");
        if (openmrsIdType != null) {
            openmrsIdType.setName("iSantePlus ID");
            patientService.savePatientIdentifierType(openmrsIdType);
        }
    }

    /**
     * Installs metadataBundles from multiple providers all found in the IsantePlusMetadataBundle.class
     * Uses the MetadataDeploy Module
     */
    private void installMetadataBundles() throws Exception{

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);

        //Deploy metadata bundle if the ConfigurableGlobalProperties.METADATA_LAST_UPDATED_DATE is not equal to IsantePlusConstants.METADATA_LAST_UPDATED_DATE
        String metadataLastUpdatedDate = Context.getAdministrationService().getGlobalProperty(ConfigurableGlobalProperties.METADATA_LAST_UPDATED_DATE);
        if (!RegistrationAppConstants.METADATA_LAST_UPDATED_DATE.equals(metadataLastUpdatedDate)) {
            log.info("installing registrationapp metadata bundle");
            deployService.installBundle(Context.getRegisteredComponents(HaitiPatientIdentifierTypeBundle.class).get(0));
            deployService.installBundle(Context.getRegisteredComponents(HaitiSedishMpiPatientIdentifierTypeBundle.class).get(0));
            deployService.installBundle(Context.getRegisteredComponents(RegistrationAppMetadataBundle.class).get(0));
        } else {
            log.info("registrationapp metadata bundle not installed");
        }


    }

    /**
     * @see ModuleActivator#willStop()
     */
    public void willStop() {

        log.info("Stopping Registration App Patient Dashboard Module");
    }

    /**
     * @see ModuleActivator#stopped()
     */
    public void stopped() {
        log.info("Registration App Patient Dashboard Module stopped");
    }

}

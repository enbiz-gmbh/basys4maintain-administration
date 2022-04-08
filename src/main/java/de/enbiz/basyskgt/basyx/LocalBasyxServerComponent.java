/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package de.enbiz.basyskgt.basyx;

import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LocalBasyxServerComponent {
	// Server URLs
	// TODO move to config
	public static final String REGISTRYPATH = "http://localhost:4000/registry";
	public static final String AASSERVERPATH = "http://localhost:4001/aasServer";

	/**
	 * Starts an empty registry at "http://localhost:4000"
	 */
	private static void startRegistry() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4000, "/registry");
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
		RegistryComponent registry = new RegistryComponent(contextConfig, registryConfig);

		// Start the created server
		registry.startComponent();
	}

	/**
	 * Startup an empty server at "http://localhost:4001/"
	 */
	private static AASServerComponent startAASServer() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4001, "/aasServer");
		BaSyxAASServerConfiguration aasServerConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "", REGISTRYPATH); //TODO change backend to DB
		AASServerComponent aasServer = new AASServerComponent(contextConfig, aasServerConfig);

		// Start the created server
		aasServer.startComponent();
		return aasServer;
	}

	@Bean
	public AASServerComponent getLocalAasServer() {
		startRegistry();
		return startAASServer();
	}
}

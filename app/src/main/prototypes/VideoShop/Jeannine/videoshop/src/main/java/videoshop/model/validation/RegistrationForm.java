/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package videoshop.model.validation;

import org.hibernate.validator.constraints.NotEmpty;

// (｡◕‿◕｡)
// Manuelle Validierung ist mühsam, Spring bietet hierfür auch Support.
// Um die Registrierung auf korrekte Eingaben zu checken, schreiben eine POJO-Klasse, welche den Inputfelder der Registrierung entspricht
// Diese werden annotiert, damit der Validator weiß, worauf geprüft werden soll
// Via message übergeben wir einen Resourcekey um die Fehlermeldungen auch internationalisierbar zu machen.
// Die ValidationMessages.properties Datei befindet sich in /src/main/resources
// Lektüre: 
// http://docs.oracle.com/javaee/6/tutorial/doc/gircz.html
// http://docs.jboss.org/hibernate/validator/4.2/reference/en-US/html/

public class RegistrationForm {

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")//
	private String name;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}")//
	private String password;

	@NotEmpty(message = "{RegistrationForm.address.NotEmpty}")//
	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}

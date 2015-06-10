/*******************************************************************************
 * Copyright 2015 DANS - Data Archiving and Networked Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package nl.knaw.dans.dccd.web.download;

import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.model.Project;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * @author paulboon
 */
public class DownloadPanel extends Panel {
	private static final long serialVersionUID = 9110250938647271835L;
	private Project project;
	private final String storeId;
	private boolean initiated;

	public static final String DOWNLOAD_XML = "download_xml";

	public static final String TRIDAS_XML_CHARSET = "UTF-8"; // maybe even on a global level?

	public DownloadPanel(final String id, Project project) {
		super(id);
		this.project = project;
		this.storeId = project.getStoreId();
	}

	@Override
	protected void onBeforeRender() {
		if (!initiated) {
			init();
			initiated = true;
		}
		super.onBeforeRender();
	}

	private void init()
	{
//		add(new ResourceLink(DOWNLOAD_XML, getXMLWebResource(project)));
		add(new ResourceLink(DOWNLOAD_XML, getXMLWebResource()));
	}

	// Produces TRiDaS which is XML in UTF-8
	private WebResource getXMLWebResource() //final Project project)
	{
		WebResource export = new WebResource() {
			private static final long serialVersionUID = -5599977621589734872L;

			@Override
			public IResourceStream getResourceStream() {

				CharSequence xml = null;
				java.io.StringWriter sw = new StringWriter();

				// get complete project from repository,
				// just overwrite any thing allready downloaded
				// convert that to TRiDaS
				//
				// Note: make it a service to get xml from a project
				try {
					// NOTE for refactoring; we only need an StroreId, not a whole project
					project = DccdDataService.getService().getProject(storeId);

					//
					JAXBContext jaxbContext = null;
					// System.out.println("\n TRiDaS XML, non valid, but with the structure");
					jaxbContext = JAXBContext.newInstance("org.tridas.schema");
					// now marshall the pruned clone
					Marshaller marshaller = jaxbContext.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_ENCODING, TRIDAS_XML_CHARSET);
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
							true);// testing
					marshaller.marshal(project.getTridas(), sw);
					// System.out.print(sw.toString());

				} catch (DataServiceException e) {
					e.printStackTrace();
					error(e.getMessage());
				} catch (JAXBException e) {
					e.printStackTrace();
					error(e.getMessage());
				}

				xml = sw.toString();
				StringResourceStream rs = new StringResourceStream(xml,"text/xml");
				rs.setCharset(Charset.forName(TRIDAS_XML_CHARSET)); // must be according to tridas
				return rs;
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);

				// construct filename
				final String XML_EXTENSION = "xml";
				String filename = project.getTitle();
				if(filename.length() == 0) filename = "tridas"; // at least have decent filename
				filename = filename + "-" + project.getSid(); // add the repository unique id?
				filename = filename + "." + XML_EXTENSION;

				response.setAttachmentHeader(filename);
			}
		};
		export.setCacheable(false);

		return export;
	}

}

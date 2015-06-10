package nl.knaw.dans.dccd.web.project;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.wicket.components.CommonGPanel;
import nl.knaw.dans.dccd.model.DccdAssociatedFileBinaryUnit;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.upload.AdditionalAssociatedFilesUploadPage;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class AssociatedFilesInfoPanel extends CommonGPanel<Project> 
{
	private static final long serialVersionUID = -3847611061957323806L;
	private static Logger logger = Logger.getLogger(AssociatedFilesInfoPanel.class);

	public AssociatedFilesInfoPanel(String id, final IModel<Project> model) 
	{
		super(id, model);

		 // put a label on it
		add(new Label("assocFilesInfo", new PropertyModel(this, "info")));
		
		// upload additional associated files
		Link<Project> uploadLink = new Link<Project>("additional_upload", model)
		{
			private static final long serialVersionUID = -4554495089557011167L;

			@Override
			public void onClick()
			{
				// navigate to upload page with given project
				setResponsePage(new AdditionalAssociatedFilesUploadPage(model));
			}

			@Override
			public boolean isVisible() {
				// only when in Draft (and only owner or admin can see Draft anyway)
				Project project = (Project) getModelObject();
				return project.getAdministrativeMetadata().getAdministrativeState().compareTo(DatasetState.DRAFT) == 0;
			}
		};
		add(uploadLink);
	}

	// construct textual information on the associated files
	public String getInfo() 
	{
		Project project = getModelObject();
		List<DccdAssociatedFileBinaryUnit> units = project.getAssociatedFileBinaryUnits();
		logger.debug("# of associated files = " + units.size());
		
		List<String> extensions = extractExtensions(units);
		HashMap<String,Integer> frequencies = calculateWordFrequencies(extensions);

		// Note that we are not using a StringResourceModel and not a repeatable 'list' container etc. 
		// but we construct the complete string with localized resources using getString.

		if (frequencies.isEmpty())
			return getString("assocFilesInfo.none");


		// get the localized strings for the GUI
		String oneFile = getString("assocFilesInfo.one");
		String multipleFiles = getString("assocFilesInfo.multiple");
		String unknownType = getString("assocFilesInfo.unknownType");
		
		StringBuilder sb = new StringBuilder();
		
		boolean isFirstEntry = true;
		for (Map.Entry<String, Integer> entry : frequencies.entrySet()) { 
			if (isFirstEntry) {
				isFirstEntry = false;
			} else {
				// prepend a seperator after a first entry
				sb.append(", ");
			}
			
			Integer numFiles = entry.getValue();
			String fileType = entry.getKey();
			
			if (fileType.isEmpty()) 
				fileType = unknownType; // no extension, so we don't know
			else
				fileType = fileType.toUpperCase(); // make it stand out!
			
			String filesOrFileStr = (numFiles > 1)?multipleFiles:oneFile; 
			// Note instead of sb.append( numFiles + " " + fileType + " " + filesOrFileStr);
			// use a template that specifies the order 
			// and can be different for each language (esp. French is different).
			sb.append(MessageFormat.format(getString("assocFilesInfo.template"), numFiles, fileType, filesOrFileStr));
		}
		
		return sb.toString();
	}

	private List<String> extractExtensions(List<DccdAssociatedFileBinaryUnit> units) 
	{
		List<String> extensions = new ArrayList<String>();
		for (DccdAssociatedFileBinaryUnit unit: units) {
			String filename = unit.getFileName();
			logger.debug("filename = " + filename);
			String extension = "";
			int dotPos = filename.lastIndexOf('.');
			if (dotPos != -1) {
				extension = filename.substring(dotPos+1, filename.length()).trim();
			}
			extensions.add(extension);
		}
		return extensions;
	}
	
	// note that it is case insensitive and returns the words in lowercase 
	private HashMap<String, Integer> calculateWordFrequencies(List<String> words)
	{
		HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
		for (String word : words){
		  word = word.toLowerCase(); // case insensitive
		  Integer num = frequencies.get(word);
		  if (num != null)
		    frequencies.put(word, num+1);
		  else
		    frequencies.put(word, 1);
		}
		return frequencies;
	}
}

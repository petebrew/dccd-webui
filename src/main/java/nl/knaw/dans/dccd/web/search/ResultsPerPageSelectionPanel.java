package nl.knaw.dans.dccd.web.search;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import nl.knaw.dans.common.wicket.components.CommonGPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.dccd.web.DccdSession;

public class ResultsPerPageSelectionPanel extends CommonGPanel<SearchRequestBuilder> 
{
	private static final long serialVersionUID = -6132677000738448676L;
	private static Logger logger = Logger.getLogger(ResultsPerPageSelectionPanel.class);

	// limited range of numbers to select from, assume the default is 10
	private List<Integer> numbers = Arrays.asList(new Integer[] { 5, 10, 20, 50, 100 });

	public ResultsPerPageSelectionPanel(String id, final IModel<SearchRequestBuilder> model) {
		super(id, model);
		
		// the selection dropdown
		add(new DropDownChoice<Integer>("resultsPerPageSelection", new PropertyModel<Integer>(model, "limit"), numbers) {
			private static final long serialVersionUID = -8656965138542091921L;

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

			@Override
			protected void onSelectionChanged(Integer newSelection) {
				super.onSelectionChanged(newSelection);
				logger.debug("selection changed");
				// always show the first page after a change
				model.getObject().setOffset(0); 
				// store it in the session
				((DccdSession)Session.get()).setResultCount(newSelection);				
			}
		});
	}

}

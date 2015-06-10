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
package nl.knaw.dans.dccd.common.wicket.geo;

import java.util.List;

import nl.knaw.dans.dccd.common.lang.geo.Marker;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When the production/calculation of the markers takes to much time 
 * and the page loading takes to long (>2sec.)
 * You want to have the page loaded and shown before the time consuming work is started. 
 * After the work is done the page is updated via AJAX
 *
 */
public abstract class LazyLoadingGeoViewer extends GeoViewer
{
	private static final Logger	logger	= LoggerFactory.getLogger(LazyLoadingGeoViewer.class);
	private static final long serialVersionUID = 6972476933936426751L;

	// The 'Loading...' indicator
	Component indicator = null;
	
	/**
	 * The only constructor that makes sense for lazy loading!
	 * 
	 * @param id
	 */
	public LazyLoadingGeoViewer(String id)
	{
		super(id);
				
		/**
		 * Note that respond is called after OnDomReady, see renderHead
		 * the AjaxLazyLoadPanel is using the same approach
		 */
		add( new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = -7720550178388585424L;

			@Override
	        protected void respond(AjaxRequestTarget target) {

				addMarkers(produceMarkers(), target);
				
				Component indicatorDone = new Label("indicator", "").setOutputMarkupId(true);
				indicatorDone.add(new SimpleAttributeModifier("style", "display:none;"));
				// Hmm, could make a hidden class, looks better, maybe there is one allready?
				
				indicator.replaceWith(indicatorDone);
				indicator = indicatorDone;
				target.addComponent(indicatorDone);
	        }

	        @Override
	        public void renderHead(IHeaderResponse response) {
	            super.renderHead( response );
	            response.renderOnDomReadyJavascript( getCallbackScript().toString() );
	        }
	    });
	}

	/**
	 * The indicator must be created here instead of in the constructor 
	 * otherwise page reload is not handled correctly
	 */
	@Override
	protected void onBeforeRender() {
		Component newIndicator = new Label("indicator", "<span>Loading...<img alt=\"Loading...\" src=\"" +
				RequestCycle.get().urlFor(AbstractDefaultAjaxBehavior.INDICATOR) + "\"/></span>")
			.setEscapeModelStrings(false)
			.setOutputMarkupId(true);
		if (indicator != null)
		{
			indicator.replaceWith(newIndicator);
		} else {
			add(newIndicator);
		}
		indicator = newIndicator;
		
		super.onBeforeRender();
	}
	
	@Override
	protected String getInitializationJS() {
		String id = getMarkupId();
		logger.debug("GeoViewer - Initializing: " + id);
		String script = "";

		// set global setting for icons/images of all viewers
		script += "geoViewer.DefaultMarkerIconPath = \'" + 
					urlFor(getDefaultMarkerIconImageReference()) + 
					"\';\n";
		
		// Could insert some extra options (Global)
		//script += "geoViewer.clustering = true;\n";
	
		// call JS function for init of the "GeoViewer" object
		//script += "geoViewer.init(\'" + id + "\');\n";
		//
		// Always cluster, because we have lots of markers
		script += "geoViewer.init(\'" + id + "\', {clustering:true});\n";
		
		return script;
	}

	/**
	 * Overide this one with your own code
	 * This would be the time consuming action for which we need the lazy loading
	 * 
	 * @return
	 */
	protected  abstract List<Marker> produceMarkers();
}

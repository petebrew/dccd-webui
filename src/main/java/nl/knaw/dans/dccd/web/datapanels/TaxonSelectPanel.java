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
package nl.knaw.dans.dccd.web.datapanels;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import nl.knaw.dans.dccd.application.services.DccdVocabularyService;
import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.vocabulary.TridasTaxonomy;
import nl.knaw.dans.dccd.model.vocabulary.TridasTaxonomy.TaxonNode;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.tridas.schema.ControlledVoc;

/**
 * @author dev
 */
public class TaxonSelectPanel extends Panel
{
	private static Logger logger = Logger.getLogger(TaxonSelectPanel.class);
	private static final long	serialVersionUID	= -652748829243312964L;
	private LinkTree taxonTree = null;
	private TreeModel treeModel;

	public TaxonSelectPanel(String id, final IModel<?> model)
	{
		super(id, model);
		
		// create a tree with taxon term's
		DefaultMutableTreeNode rootNode = buildTree();
		treeModel = new DefaultTreeModel(rootNode);
		taxonTree = new LinkTree("taxonTree", treeModel)
		{
			private static final long	serialVersionUID	= 537830733502169480L;

			@Override
			protected void onNodeLinkClicked(Object node, BaseTree tree, AjaxRequestTarget target)
			{
				// TODO Auto-generated method stub
				//super.onNodeLinkClicked(node, tree, target);

	           	 if (!tree.getTreeState().isNodeSelected(node)) {
            		 logger.debug("node is deselected, reselecting in order to disable this!");
            		 // select again, forcing single selection!
            		 // it is a hack, when clicking doesn't work it should not
            		 // look clickable...
            		 tree.getTreeState().selectNode(node, true);
            		 return;
            	 }
	           	 
	            // I know it's a UITaxonTerm
	           	UITaxonTerm nodeModel = (UITaxonTerm) ((DefaultMutableTreeNode)node).getUserObject();
	           	logger.debug("Tree selection: " + nodeModel.getValue()); 
	           	
	           	// change the model...
				EntityAttribute attr = (EntityAttribute) model.getObject();
				if (attr != null)
				{
					// assume object is a ControlledVoc
					ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
					cvoc.setValue(nodeModel.getValue());
				}
				
            	// let others handle the change
            	onSelectionChanged(target);
			}
		};
		add(taxonTree);
		// Always have the toplevel of the tree expanded, 
		// so it looks like  a tree
		taxonTree.getTreeState().expandNode(rootNode);
		
		EntityAttribute attr = (EntityAttribute) model.getObject();
		if (attr != null)
		{
			ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
			if (cvoc != null && cvoc.isSetValue())
				selectTerm(cvoc.getValue());
		}
	}

	/**
	 * Called when selection (node in tree) has changed
	 * override this to do your own handling
	 */
	protected void onSelectionChanged(AjaxRequestTarget target) 
	{
		// empty!
	}			
	
	@SuppressWarnings("unchecked")
	private TreeNode getSelectedNode() {
		Collection<Object> nodes = taxonTree.getTreeState().getSelectedNodes();
		for (Object node : nodes) {
	       	return (TreeNode) node; // first one, should be only one anyway!
		}
		//logger
		return null; // there is none
	}
	
	protected String getSelectionAsString()
	{
		// I know it's a UITaxonTerm
		TreeNode selectedNode = getSelectedNode();
		if (selectedNode != null)
		{
			UITaxonTerm nodeModel = (UITaxonTerm) ((DefaultMutableTreeNode)selectedNode).getUserObject();
			//logger.debug("Tree selection: " + nodeModel.getValue()); 
			return nodeModel.getValue();
		}
		else
			return "";
	}
	
	/**
	 * Select the tree node for the given term;
	 * results in an expanded node looking 'selected'
	 *
	 * @param termString The term to select
	 */
	public void selectTerm(String termString) {
		if (termString.trim().isEmpty()) 
		{
			deselect();
		}
		else
		{			
			// find entity in the tree
			TreeNode root = (TreeNode)treeModel.getRoot();
			TreeNode node = findSubnodeWithTerm(root, termString);
			ITreeState treeState = taxonTree.getTreeState();
	
			if (node != null) {
				// OK, we found it
				treeState.selectNode(node, true);
				expandeNodePath(node);
			} else {
				logger.warn("Could not find term: " + termString);
				deselect();
			}
		}
	}

	// deselect all selected nodes
	public void deselect()
	{
		ITreeState treeState = taxonTree.getTreeState();
		Collection<Object> selectedNodes = treeState.getSelectedNodes();
		for (Object selectedNode : selectedNodes)
		{
			treeState.selectNode(selectedNode, false);
		}
	}
	
	/**
	 *
	 * @param node Node to expand including the path of nodes up to the root
	 */
	public void expandeNodePath(TreeNode node) {
		ITreeState treeState = taxonTree.getTreeState();
		treeState.expandNode(node);

		// but now the node might not be visible because it's parents are not expanded
		// therefore expand all parents

		TreeNode parent = node.getParent();
		while (parent != null ) {
			treeState.expandNode(parent);
			parent = parent.getParent();
		}
	}

	/**
	 * Find the sub node (recursively) representing the entity with the given id
	 *
	 * @param node
	 * @param entityId
	 * @return The found node or null if nothing found
	 */
	@SuppressWarnings("unchecked")
	public TreeNode findSubnodeWithTerm(TreeNode node, String termString) {
		if (node == null) throw new IllegalArgumentException("TreeNode was null");

		TreeNode nodeFound = null;
		for (final Enumeration e = node.children();e.hasMoreElements();) {
	        TreeNode child = ((TreeNode) e.nextElement());
	        
	        UITaxonTerm taxonTerm = (UITaxonTerm) ((DefaultMutableTreeNode)child).getUserObject();
			
			String childNodetermString = taxonTerm.getValue();
			
			// test
			if (termString.compareTo(childNodetermString) == 0) {
				nodeFound = child;
				break; //only one, so we found it
			}
			
			//Recursive !
			nodeFound = findSubnodeWithTerm(child, termString);
			if (nodeFound != null)
				break;
	    }

		return nodeFound;
	}	
	// TODO use the taxonomy to build the (Wicket UI) Tree
	public DefaultMutableTreeNode buildTree()
	{
		// we need the taxonomy to build the UI tree
		TridasTaxonomy elementTaxonTaxonomy = DccdVocabularyService.getService().getElementTaxonTaxonomy();
		TaxonNode rootTaxonNode = elementTaxonTaxonomy.getRootNode();
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new UITaxonTerm(rootTaxonNode.getName()));
		
		DefaultMutableTreeNode parentNode = rootNode;
		List<TaxonNode> subTaxonNodes = rootTaxonNode.getSubNodes();		
		for(TaxonNode subTaxonNode : subTaxonNodes)
		{
			DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(new UITaxonTerm(subTaxonNode.getName()));
			parentNode.add(objNode);
			buildSubTree(objNode, subTaxonNode);//recurse
		}
		
		//DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new UITaxonTerm("ROOT"));
		//
		//DefaultMutableTreeNode parentNode = rootNode;
		//DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(new UITaxonTerm("CHILD"));
		//parentNode.add(objNode);
		
		return rootNode;
	}
	
	private void buildSubTree(DefaultMutableTreeNode parentNode, TaxonNode taxonNode)
	{
		List<TaxonNode> subTaxonNodes = taxonNode.getSubNodes();		
		for(TaxonNode subTaxonNode : subTaxonNodes)
		{
			DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(new UITaxonTerm(subTaxonNode.getName()));
			parentNode.add(objNode);
			buildSubTree(objNode, subTaxonNode);//recurse
		}		
	}
	
	// user object for the tree nodes
	private class UITaxonTerm implements Serializable
	{
		private static final long	serialVersionUID	= 8875094001446289778L;
		private String value;
		
		public UITaxonTerm(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return getValue().toString();
		}
		
	}
}


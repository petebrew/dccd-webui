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
package nl.knaw.dans.dccd.web.entitytree;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import nl.knaw.dans.dccd.common.web.behavior.ConditionalLinkConfirmationBehavior;
import nl.knaw.dans.dccd.model.EntityTree;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.entities.ProjectEntity;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author paulboon
 */
public class EntityTreePanel extends Panel {
	private static final long serialVersionUID = -6312236772159326456L;
	private static Logger logger = Logger.getLogger(EntityTreePanel.class);
	private boolean entitiesEditable = false;
	private LinkTree linkTree = null;
	private TreeModel treeModel;

	public EntityTreePanel(String id, 
			IModel model, 
			final ProjectPermissionLevel permissionLevel) 
	{
		this(id, model, permissionLevel, false); // uneditable entities by default
	}

	public EntityTreePanel(String id, IModel model, 
			final ProjectPermissionLevel permissionLevel, 
			final boolean entitiesEditable) 
	{
		super(id, model);
		this.entitiesEditable = entitiesEditable;

		// assume DendroProject is the model object
		Project project = (Project)model.getObject();
		EntityTree entityTree = project.entityTree;

		// build the UI tree model
		EntityUITreeBuilder treeBuilder =  new EntityUITreeBuilder(entitiesEditable);
		ProjectEntity projectEntity = entityTree.getProjectEntity();
		DefaultMutableTreeNode rootNode = treeBuilder.buildProjectUINodesTree(projectEntity, permissionLevel);
		treeModel = new DefaultTreeModel(rootNode);

		// create the UI tree with the model
		linkTree = new LinkTree("tree", treeModel) {
			private static final long serialVersionUID = 8851142522578546998L;

			@Override
            protected void onNodeLinkClicked(Object node,
                    BaseTree tree,
                    AjaxRequestTarget target) {

            	 if (!tree.getTreeState().isNodeSelected(node)) {
            		 logger.debug("node is deselected, reselecting in order to disable this!");
            		 // select again, forcing single selection!
            		 // it is a hack, when clicking doesn't work it should not
            		 // look clickable...
            		 tree.getTreeState().selectNode(node, true);
            		 return;
            	 }
            	 
            	//logger.info("clicked: " + node);
            	// the resources are in DccdAttrPanel.xml etc. ?
            	ResourceModel nameModel = new ResourceModel("entity_name_"+node.toString(), node.toString());
            	logger.debug("clicked: " + nameModel.getObject().toString());

            	// I know it's a DefaultMutableTreeNode
            	EntityUITreeNodeModel nodeModel = (EntityUITreeNodeModel) ((DefaultMutableTreeNode)node).getUserObject();

            	// let others do their thing
            	onSelectionChanged(nodeModel, tree, target);
            }
			
			
			// When we edit in a form we might have unsaved changes, 
			// add a behavior on each link to show a confirm dialog
			@Override
			public MarkupContainer newLink(String id, ILinkCallback callback)
			{
				MarkupContainer newLink = super.newLink(id, callback);
				
				if (entitiesEditable)
				{
					String condition = "formOnRender != (formBeforeSubmit=Wicket.Form.doSerialize(getFormWatched()))"; 
					String message = getString("unsavedchanges_entitychange_message");
					
					newLink.add(new ConditionalLinkConfirmationBehavior(message, condition));
				}
				return newLink;
			}

			/*
			// Trying to change the looks of invalid nodes... 
			// extending AttributeModifier might be what we want... but with css it is tricky
			class AddCSSClassBehavior extends AbstractBehavior {
				private static final long	serialVersionUID	= 1L;

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					super.onComponentTag(component, tag);
					
					//String classes = tag.getAttributes().getString("class");
					//logger.debug("CSS Class: " + classes);
					logger.debug("---> Changing tag: " + tag.getName());
					
					// As a test just set the background to yellow
					tag.getAttributes().put("style", "background-color:yellow;");
					// but we don't have control over the content inside
					// We could have a JavaScript (jQuery possibly) 
					// to fix all the inside of the tags marked wit the class	
				}
			};
			@Override
			protected Component newNodeComponent(String id, IModel<Object> model)
			{
				Component component = super.newNodeComponent(id, model);
				//logger.debug("---> id: " + id); 
				
				// Note: these are the table cells on the row with the link
				component.add(new AddCSSClassBehavior());
				
				return component;
			}
			*/
			
		};
		//linkTree.getTreeState().expandAll();
		// Have the toplevel of the tree expanded
		linkTree.getTreeState().expandNode(rootNode);

		add(linkTree);

		// selection, initial
		linkTree.getTreeState().selectNode(rootNode, true);
		
		//--- Tree expand/collapse 
		
		add(new AjaxLink("expandAll")
		{
			private static final long	serialVersionUID	= 1L;
			@Override
		    public void onClick(AjaxRequestTarget target)
		    {
		    	linkTree.getTreeState().expandAll();
		    	linkTree.updateTree(target);
		    }
		});
		
		add(new AjaxLink("collapseAll")
		{
			private static final long	serialVersionUID	= 1L;
		    @Override
		    public void onClick(AjaxRequestTarget target)
		    {
		    	linkTree.getTreeState().collapseAll();
		    	linkTree.updateTree(target);
		    }
		});
		
	}

	/**
	 * Called when selection of entity (node in tree) has changed
	 * override this to do your own handling
	 */
	public void onSelectionChanged(EntityUITreeNodeModel nodeModel,
			BaseTree tree,
	        AjaxRequestTarget target) {
		// empty!
	}

	@SuppressWarnings("unchecked")
	private TreeNode getSelectedNode() {
		Collection<Object> nodes = linkTree.getTreeState().getSelectedNodes();
		for (Object node : nodes) {
	       	return (TreeNode) node; // first one, should be only one anyway!
		}
		//logger
		return null; // there is none
	}

	// get the current selected node
	public EntityUITreeNodeModel getSelectedModel() {
		TreeNode node = getSelectedNode();
		// I know it's a DefaultMutableTreeNode
		EntityUITreeNodeModel nodeModel = (EntityUITreeNodeModel) ((DefaultMutableTreeNode)node).getUserObject();
		return nodeModel;

		/*
		Collection<TreeNode> nodes = linkTree.getTreeState().getSelectedNodes();
		for (TreeNode node : nodes) {
	    	// I know it's a DefaultMutableTreeNode
	    	DccdEntityTreeNodeModel nodeModel = (DccdEntityTreeNodeModel) ((DefaultMutableTreeNode)node).getUserObject();
	    	return nodeModel; // first one!
		}

		return null; // there is none
		*/
	}

	/**
	 *
	 * @param upLevels Number of levels to move upward
	 * @param target
	 */
	public void MoveSelectionUp(int upLevels, AjaxRequestTarget target) {
		// assume always one and only one node selected (single selection mode)
		// go up the tree and select the node that is the given levels upward
		TreeNode node = getSelectedNode();
		if (node == null) {
			// something wrong, should always have a node selected
			//logger.
			return;
		}

		// upLevels should be positive number
		if (upLevels < 1) {
			// warn!
			logger.warn("upLevels parameter was out of range: " + upLevels + " should be bigger than 0");
			return;
		}

		// go 'up' the tree several levels
		TreeNode newNode = node;
		int levelsToGoUp = upLevels;
		while (levelsToGoUp > 0 && newNode != null) {
			newNode = newNode.getParent(); // go up one
			--levelsToGoUp;
		}

		expandeNodePath(newNode); // make sure the selected node is visible
		linkTree.getTreeState().selectNode(newNode, true);
		linkTree.updateTree(target);

		// Propagate change
		// I know it's a DefaultMutableTreeNode
		EntityUITreeNodeModel nodeModel = (EntityUITreeNodeModel) ((DefaultMutableTreeNode)newNode).getUserObject();
		// let others do their thing
		onSelectionChanged(nodeModel, linkTree, target);//???

	}

	/** get Path (from tree root to selection) as a String List
	 *
	 */
	public List<String> getSelectionPathAsStringList() {
		// use linkedlist for prepending
		LinkedList<String> list = new LinkedList<String>();

		TreeNode node = getSelectedNode();
		while (node != null) {
			EntityUITreeNodeModel nodeModel = (EntityUITreeNodeModel) ((DefaultMutableTreeNode)node).getUserObject();
	    	//logger.info("level: " + nodeModel.getName());//toString());
	    	// add to the list begining
	    	list.addFirst(nodeModel.getName());
			node = node.getParent(); // go up one
		}
		return list; // as a List and not a linked list
	}

	/**
	 * Select the tree node for the given entity;
	 * results in an expanded node looking 'selected'
	 *
	 * @param entityId The Id of the entity to select
	 */
	public void selectEntity(String entityId) {
		// find entity in the tree
		TreeNode root = (TreeNode)treeModel.getRoot();
		TreeNode entityNode = findSubnodeWithEntity(root, entityId);

		if (entityNode != null) {
			// OK, we found it
			ITreeState treeState = linkTree.getTreeState();

			treeState.selectNode(entityNode, true);
			expandeNodePath(entityNode);
		} else {
			logger.warn("Could not find entity with id: " + entityId);
		}
	}

	/**
	 *
	 * @param entityNode Node to expand including the path of nodes up to the root
	 */
	public void expandeNodePath(TreeNode entityNode) {
		ITreeState treeState = linkTree.getTreeState();
		treeState.expandNode(entityNode);

		// but now the node might not be visible because it's parents are not expanded
		// therefore expand all parents

		TreeNode parent = entityNode.getParent();
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
	public TreeNode findSubnodeWithEntity(TreeNode node, String entityId) {
		if (node == null) throw new IllegalArgumentException("TreeNode was null");

		TreeNode nodeFound = null;
		for (final Enumeration e = node.children();e.hasMoreElements();) {
	        TreeNode child = ((TreeNode) e.nextElement());
			EntityUITreeNodeModel nodeModel = (EntityUITreeNodeModel) ((DefaultMutableTreeNode)child).getUserObject();
			String childNodeEntityId = nodeModel.getEntity().getId();
			// test
			if (entityId.compareTo(childNodeEntityId) == 0) {
				nodeFound = child;
				break; //only one, so we found it
			}
			//Recursive !
			nodeFound = findSubnodeWithEntity(child, entityId);
			if (nodeFound != null)
				break;
	    }

		return nodeFound;
	}
}


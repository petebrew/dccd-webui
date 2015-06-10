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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.entities.DerivedSeriesEntity;
import nl.knaw.dans.dccd.model.entities.ElementEntity;
import nl.knaw.dans.dccd.model.entities.Entity;
import nl.knaw.dans.dccd.model.entities.ObjectEntity;

/**
 * Build UI tree from DendroEntity (tree)
 *
 * @author paulboon
 *
 */
public class EntityUITreeBuilder
{
	private boolean entitiesEditable = false;

	public EntityUITreeBuilder(final boolean entitiesEditable)
	{
		this.entitiesEditable = entitiesEditable;
	}

	// note: Could have been static, if it wasn't for the entitiesEditable
	// switch
	public DefaultMutableTreeNode buildProjectUINodesTree(final Entity entity,
			final ProjectPermissionLevel permissionLevel)
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
	
		// add nodes for Entities for this Project
		// objects
		List<Entity> subEntities = entity.getDendroEntities();
		for (Entity subEntity : subEntities)
		{
			// is it an DendroObjectEntity?
			if (subEntity.getClass().equals(ObjectEntity.class))
			{
				buildObjectUINodesTree(rootNode, subEntity, permissionLevel);
			}
		}
		// derived series
		for (Entity subEntity : subEntities)
		{
			// is it an DendroDerivedSeriesEntity?
			if (subEntity.getClass().equals(DerivedSeriesEntity.class))
			{
				buildDerivedSeriesUINodesTree(rootNode, subEntity,
						permissionLevel);
			}
		}
		return rootNode;
	}

	private void buildObjectUINodesTree(DefaultMutableTreeNode parentNode,
			final Entity entity, final ProjectPermissionLevel permissionLevel)
	{
		// add the node for this object
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
		parentNode.add(objNode);

		// sub nodes

		// add sub-objects..
		List<Entity> subEntities = entity.getDendroEntities();
		for (Entity subEntity : subEntities)
		{
			// is it an DendroObjectEntity?
			if (subEntity.getClass().equals(ObjectEntity.class))
			{
				// recursion!
				buildObjectUINodesTree(objNode, subEntity, permissionLevel);
			}
		}
		// elements
		for (Entity subEntity : subEntities)
		{
			// is it an DendroElementEntity?
			if (subEntity.getClass().equals(ElementEntity.class))
			{
				buildElementUINodesTree(objNode, subEntity, permissionLevel);
			}
		}
	}

	private void buildElementUINodesTree(DefaultMutableTreeNode parentNode,
			final Entity entity, final ProjectPermissionLevel permissionLevel)
	{
		// add the node for this object
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
		parentNode.add(objNode);

		// sub nodes
		List<Entity> subEntities = entity.getDendroEntities();
		for (Entity subEntity : subEntities)
		{
			buildSampleUINodesTree(objNode, subEntity, permissionLevel);
		}
	}

	private void buildSampleUINodesTree(DefaultMutableTreeNode parentNode,
			final Entity entity, final ProjectPermissionLevel permissionLevel)
	{
		// add the node for this object
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
		parentNode.add(objNode);

		// sub nodes
		List<Entity> subEntities = entity.getDendroEntities();
		for (Entity subEntity : subEntities)
		{
			buildRadiusUINodesTree(objNode, subEntity, permissionLevel);
		}
	}

	private void buildRadiusUINodesTree(DefaultMutableTreeNode parentNode,
			final Entity entity, final ProjectPermissionLevel permissionLevel)
	{
		// add the node for this object
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
		parentNode.add(objNode);

		// sub nodes
		List<Entity> subEntities = entity.getDendroEntities();
		for (Entity subEntity : subEntities)
		{
			buildMeasurementSeriesUINodesTree(objNode, subEntity,
					permissionLevel);
		}
	}

	private void buildMeasurementSeriesUINodesTree(
			DefaultMutableTreeNode parentNode, final Entity entity,
			final ProjectPermissionLevel permissionLevel)
	{
		// add the node for this object
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
		parentNode.add(objNode);

		// sub nodes?
		// List<DendroEntity> subEntities = entity.getDendroEntities();
		// for (DendroEntity subEntity : subEntities) {
		// buildValuesUINodesTree(objNode, subEntity);
		// }
	}

	private void buildDerivedSeriesUINodesTree(
			DefaultMutableTreeNode parentNode, final Entity entity,
			final ProjectPermissionLevel permissionLevel)
	{
		// add the node for this object
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(
				new EntityUITreeNodeModel(entity, "entity_panel",
						permissionLevel, entitiesEditable));
		parentNode.add(objNode);

		// sub nodes?
		// List<DendroEntity> subEntities = entity.getDendroEntities();
		// for (DendroEntity subEntity : subEntities) {
		// buildValuesUINodesTree(objNode, subEntity);
		// }
	}

}

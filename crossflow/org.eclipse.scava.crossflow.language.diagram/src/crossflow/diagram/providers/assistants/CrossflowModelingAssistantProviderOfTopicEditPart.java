/*
 * 
 */
package crossflow.diagram.providers.assistants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;

import crossflow.diagram.edit.parts.CommitmentTaskEditPart;
import crossflow.diagram.edit.parts.CsvSinkEditPart;
import crossflow.diagram.edit.parts.CsvSourceEditPart;
import crossflow.diagram.edit.parts.OpinionatedTaskEditPart;
import crossflow.diagram.edit.parts.ReusableComponentEditPart;
import crossflow.diagram.edit.parts.ScriptedTaskEditPart;
import crossflow.diagram.edit.parts.SinkEditPart;
import crossflow.diagram.edit.parts.SourceEditPart;
import crossflow.diagram.edit.parts.TaskEditPart;
import crossflow.diagram.edit.parts.TopicEditPart;
import crossflow.diagram.edit.parts.TypeEditPart;
import crossflow.diagram.providers.CrossflowElementTypes;
import crossflow.diagram.providers.CrossflowModelingAssistantProvider;

/**
 * @generated
 */
public class CrossflowModelingAssistantProviderOfTopicEditPart extends CrossflowModelingAssistantProvider {

	/**
	* @generated
	*/
	@Override

	public List<IElementType> getRelTypesOnSource(IAdaptable source) {
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source.getAdapter(IGraphicalEditPart.class);
		return doGetRelTypesOnSource((TopicEditPart) sourceEditPart);
	}

	/**
	* @generated
	*/
	public List<IElementType> doGetRelTypesOnSource(TopicEditPart source) {
		List<IElementType> types = new ArrayList<IElementType>(2);
		types.add(CrossflowElementTypes.StreamType_4001);
		types.add(CrossflowElementTypes.StreamInputOf_4005);
		return types;
	}

	/**
	* @generated
	*/
	@Override

	public List<IElementType> getRelTypesOnSourceAndTarget(IAdaptable source, IAdaptable target) {
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source.getAdapter(IGraphicalEditPart.class);
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target.getAdapter(IGraphicalEditPart.class);
		return doGetRelTypesOnSourceAndTarget((TopicEditPart) sourceEditPart, targetEditPart);
	}

	/**
	* @generated
	*/
	public List<IElementType> doGetRelTypesOnSourceAndTarget(TopicEditPart source, IGraphicalEditPart targetEditPart) {
		List<IElementType> types = new LinkedList<IElementType>();
		if (targetEditPart instanceof TypeEditPart) {
			types.add(CrossflowElementTypes.StreamType_4001);
		}
		if (targetEditPart instanceof CsvSourceEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof CsvSinkEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof SourceEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof SinkEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof CommitmentTaskEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof OpinionatedTaskEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof ScriptedTaskEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof ReusableComponentEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		if (targetEditPart instanceof TaskEditPart) {
			types.add(CrossflowElementTypes.StreamInputOf_4005);
		}
		return types;
	}

	/**
	* @generated
	*/
	@Override

	public List<IElementType> getTypesForTarget(IAdaptable source, IElementType relationshipType) {
		IGraphicalEditPart sourceEditPart = (IGraphicalEditPart) source.getAdapter(IGraphicalEditPart.class);
		return doGetTypesForTarget((TopicEditPart) sourceEditPart, relationshipType);
	}

	/**
	* @generated
	*/
	public List<IElementType> doGetTypesForTarget(TopicEditPart source, IElementType relationshipType) {
		List<IElementType> types = new ArrayList<IElementType>();
		if (relationshipType == CrossflowElementTypes.StreamType_4001) {
			types.add(CrossflowElementTypes.Type_2011);
		} else if (relationshipType == CrossflowElementTypes.StreamInputOf_4005) {
			types.add(CrossflowElementTypes.CsvSource_2001);
			types.add(CrossflowElementTypes.CsvSink_2002);
			types.add(CrossflowElementTypes.Source_2005);
			types.add(CrossflowElementTypes.Sink_2006);
			types.add(CrossflowElementTypes.CommitmentTask_2007);
			types.add(CrossflowElementTypes.OpinionatedTask_2008);
			types.add(CrossflowElementTypes.ScriptedTask_2015);
			types.add(CrossflowElementTypes.ReusableComponent_2017);
			types.add(CrossflowElementTypes.Task_2010);
		}
		return types;
	}

	/**
	* @generated
	*/
	@Override

	public List<IElementType> getRelTypesOnTarget(IAdaptable target) {
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target.getAdapter(IGraphicalEditPart.class);
		return doGetRelTypesOnTarget((TopicEditPart) targetEditPart);
	}

	/**
	* @generated
	*/
	public List<IElementType> doGetRelTypesOnTarget(TopicEditPart target) {
		List<IElementType> types = new ArrayList<IElementType>(1);
		types.add(CrossflowElementTypes.TaskOutput_4003);
		return types;
	}

	/**
	* @generated
	*/
	@Override

	public List<IElementType> getTypesForSource(IAdaptable target, IElementType relationshipType) {
		IGraphicalEditPart targetEditPart = (IGraphicalEditPart) target.getAdapter(IGraphicalEditPart.class);
		return doGetTypesForSource((TopicEditPart) targetEditPart, relationshipType);
	}

	/**
	* @generated
	*/
	public List<IElementType> doGetTypesForSource(TopicEditPart target, IElementType relationshipType) {
		List<IElementType> types = new ArrayList<IElementType>();
		if (relationshipType == CrossflowElementTypes.TaskOutput_4003) {
			types.add(CrossflowElementTypes.CsvSource_2001);
			types.add(CrossflowElementTypes.CsvSink_2002);
			types.add(CrossflowElementTypes.Source_2005);
			types.add(CrossflowElementTypes.Sink_2006);
			types.add(CrossflowElementTypes.CommitmentTask_2007);
			types.add(CrossflowElementTypes.OpinionatedTask_2008);
			types.add(CrossflowElementTypes.ScriptedTask_2015);
			types.add(CrossflowElementTypes.ReusableComponent_2017);
			types.add(CrossflowElementTypes.Task_2010);
		}
		return types;
	}

}

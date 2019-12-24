/**
 */
package crossflow.provider;


import crossflow.CrossflowFactory;
import crossflow.CrossflowPackage;
import crossflow.Workflow;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link crossflow.Workflow} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class WorkflowItemProvider 
	extends ItemProviderAdapter
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkflowItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addNamePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Workflow_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Workflow_name_feature", "_UI_Workflow_type"),
				 CrossflowPackage.Literals.WORKFLOW__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(CrossflowPackage.Literals.WORKFLOW__STREAMS);
			childrenFeatures.add(CrossflowPackage.Literals.WORKFLOW__TASKS);
			childrenFeatures.add(CrossflowPackage.Literals.WORKFLOW__TYPES);
			childrenFeatures.add(CrossflowPackage.Literals.WORKFLOW__PARAMETERS);
			childrenFeatures.add(CrossflowPackage.Literals.WORKFLOW__LANGUAGES);
			childrenFeatures.add(CrossflowPackage.Literals.WORKFLOW__SERIALIZER);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns Workflow.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Workflow"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((Workflow)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_Workflow_type") :
			getString("_UI_Workflow_type") + " " + label;
	}


	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(Workflow.class)) {
			case CrossflowPackage.WORKFLOW__NAME:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case CrossflowPackage.WORKFLOW__STREAMS:
			case CrossflowPackage.WORKFLOW__TASKS:
			case CrossflowPackage.WORKFLOW__TYPES:
			case CrossflowPackage.WORKFLOW__PARAMETERS:
			case CrossflowPackage.WORKFLOW__LANGUAGES:
			case CrossflowPackage.WORKFLOW__SERIALIZER:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__STREAMS,
				 CrossflowFactory.eINSTANCE.createTopic()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__STREAMS,
				 CrossflowFactory.eINSTANCE.createQueue()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createTask()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createSource()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createCsvSource()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createSink()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createCsvSink()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createCommitmentTask()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createOpinionatedTask()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createScriptedTask()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TASKS,
				 CrossflowFactory.eINSTANCE.createReusableComponent()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__TYPES,
				 CrossflowFactory.eINSTANCE.createType()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__PARAMETERS,
				 CrossflowFactory.eINSTANCE.createDataField()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__PARAMETERS,
				 CrossflowFactory.eINSTANCE.createEnumField()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__LANGUAGES,
				 CrossflowFactory.eINSTANCE.createLanguage()));

		newChildDescriptors.add
			(createChildParameter
				(CrossflowPackage.Literals.WORKFLOW__SERIALIZER,
				 CrossflowFactory.eINSTANCE.createSerializer()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return CrossflowEditPlugin.INSTANCE;
	}

}

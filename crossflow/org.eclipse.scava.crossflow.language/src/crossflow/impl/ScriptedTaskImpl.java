/**
 */
package crossflow.impl;

import crossflow.CrossflowPackage;
import crossflow.Field;
import crossflow.ScriptedTask;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Scripted Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link crossflow.impl.ScriptedTaskImpl#getScriptingLanguage <em>Scripting Language</em>}</li>
 *   <li>{@link crossflow.impl.ScriptedTaskImpl#getScript <em>Script</em>}</li>
 *   <li>{@link crossflow.impl.ScriptedTaskImpl#getOutputVariables <em>Output Variables</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ScriptedTaskImpl extends TaskImpl implements ScriptedTask {
	/**
	 * The default value of the '{@link #getScriptingLanguage() <em>Scripting Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScriptingLanguage()
	 * @generated
	 * @ordered
	 */
	protected static final String SCRIPTING_LANGUAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScriptingLanguage() <em>Scripting Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScriptingLanguage()
	 * @generated
	 * @ordered
	 */
	protected String scriptingLanguage = SCRIPTING_LANGUAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getScript() <em>Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScript()
	 * @generated
	 * @ordered
	 */
	protected static final String SCRIPT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScript() <em>Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScript()
	 * @generated
	 * @ordered
	 */
	protected String script = SCRIPT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOutputVariables() <em>Output Variables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputVariables()
	 * @generated
	 * @ordered
	 */
	protected EList<Field> outputVariables;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ScriptedTaskImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CrossflowPackage.Literals.SCRIPTED_TASK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getScriptingLanguage() {
		return scriptingLanguage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScriptingLanguage(String newScriptingLanguage) {
		String oldScriptingLanguage = scriptingLanguage;
		scriptingLanguage = newScriptingLanguage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CrossflowPackage.SCRIPTED_TASK__SCRIPTING_LANGUAGE, oldScriptingLanguage, scriptingLanguage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getScript() {
		return script;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setScript(String newScript) {
		String oldScript = script;
		script = newScript;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CrossflowPackage.SCRIPTED_TASK__SCRIPT, oldScript, script));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Field> getOutputVariables() {
		if (outputVariables == null) {
			outputVariables = new EObjectContainmentEList<Field>(Field.class, this, CrossflowPackage.SCRIPTED_TASK__OUTPUT_VARIABLES);
		}
		return outputVariables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CrossflowPackage.SCRIPTED_TASK__OUTPUT_VARIABLES:
				return ((InternalEList<?>)getOutputVariables()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CrossflowPackage.SCRIPTED_TASK__SCRIPTING_LANGUAGE:
				return getScriptingLanguage();
			case CrossflowPackage.SCRIPTED_TASK__SCRIPT:
				return getScript();
			case CrossflowPackage.SCRIPTED_TASK__OUTPUT_VARIABLES:
				return getOutputVariables();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CrossflowPackage.SCRIPTED_TASK__SCRIPTING_LANGUAGE:
				setScriptingLanguage((String)newValue);
				return;
			case CrossflowPackage.SCRIPTED_TASK__SCRIPT:
				setScript((String)newValue);
				return;
			case CrossflowPackage.SCRIPTED_TASK__OUTPUT_VARIABLES:
				getOutputVariables().clear();
				getOutputVariables().addAll((Collection<? extends Field>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case CrossflowPackage.SCRIPTED_TASK__SCRIPTING_LANGUAGE:
				setScriptingLanguage(SCRIPTING_LANGUAGE_EDEFAULT);
				return;
			case CrossflowPackage.SCRIPTED_TASK__SCRIPT:
				setScript(SCRIPT_EDEFAULT);
				return;
			case CrossflowPackage.SCRIPTED_TASK__OUTPUT_VARIABLES:
				getOutputVariables().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case CrossflowPackage.SCRIPTED_TASK__SCRIPTING_LANGUAGE:
				return SCRIPTING_LANGUAGE_EDEFAULT == null ? scriptingLanguage != null : !SCRIPTING_LANGUAGE_EDEFAULT.equals(scriptingLanguage);
			case CrossflowPackage.SCRIPTED_TASK__SCRIPT:
				return SCRIPT_EDEFAULT == null ? script != null : !SCRIPT_EDEFAULT.equals(script);
			case CrossflowPackage.SCRIPTED_TASK__OUTPUT_VARIABLES:
				return outputVariables != null && !outputVariables.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (scriptingLanguage: ");
		result.append(scriptingLanguage);
		result.append(", script: ");
		result.append(script);
		result.append(')');
		return result.toString();
	}

} //ScriptedTaskImpl

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.processengine.transformer.bpmnhybris.tests;

import de.hybris.platform.processengine.definition.xml.Action;
import de.hybris.platform.processengine.definition.xml.Case;
import de.hybris.platform.processengine.definition.xml.Choice;
import de.hybris.platform.processengine.definition.xml.ContextParameter;
import de.hybris.platform.processengine.definition.xml.End;
import de.hybris.platform.processengine.definition.xml.Process;
import de.hybris.platform.processengine.definition.xml.Script;
import de.hybris.platform.processengine.definition.xml.ScriptAction;
import de.hybris.platform.processengine.definition.xml.Transition;
import de.hybris.platform.processengine.definition.xml.Wait;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Assert;


public class HybrisProcessComparisionUtil
{

	public boolean compare(final Process directHybProcess, final Process transformedHybrisProcess)
	{
		final String dProcessName = directHybProcess.getName();
		final String trProcessName = transformedHybrisProcess.getName();
		Assert.assertEquals(dProcessName, trProcessName);

		final String dProcessClassName = directHybProcess.getProcessClass();
		final String trProcessClassName = transformedHybrisProcess.getProcessClass();
		Assert.assertEquals(dProcessClassName, trProcessClassName);

		final Map<String, Object> fromHybrisProcessNodes = createNodeLookup(directHybProcess);
		final Map<String, Object> toHybrisProcessNodes = createNodeLookup(transformedHybrisProcess);

		final int fromSize = fromHybrisProcessNodes.size();
		final int toSize = toHybrisProcessNodes.size();

		if (fromSize != toSize)
		{
			final Iterator<Entry<String, Object>> iterator = fromHybrisProcessNodes.entrySet().iterator();
			while (iterator.hasNext())
			{
				final String key = iterator.next().getKey();
				final Object graphNode = toHybrisProcessNodes.get(key);
				Assert.assertNotNull("Node not found for ID " + key, graphNode);
			}
			return false;
		}
		else
		{
			final Iterator<Entry<String, Object>> it = fromHybrisProcessNodes.entrySet().iterator();
			boolean isSame = true;
			while (it.hasNext())
			{
				final Entry<String, Object> entry = it.next();
				final String nodeId = entry.getKey();
				final Object fromNode = entry.getValue();
				Object toNode = toHybrisProcessNodes.get(nodeId);
				if (toNode == null)
				{
					toNode = toHybrisProcessNodes.get(nodeId + "IME");//In case ID changed to IME for wait w/ timeout
				}
				if (toNode == null)
				{
					Assert.fail(
							"Could not find node corresponding to node id from original: " + nodeId + " in " + toHybrisProcessNodes);
					Iterator<Entry<String, Object>> toIterator = toHybrisProcessNodes.entrySet().iterator();
					while (toIterator.hasNext())
					{
						System.out.println(toIterator.next().getKey());
					}
				}
				isSame = compare(fromNode, toNode);
				Assert.assertTrue("Nodes with ID " + nodeId + " are not same", isSame);
				/*
				 * if(!isSame){
				 * 
				 * break; }
				 */
			}

			final List<ContextParameter> dContextParameters = directHybProcess.getContextParameter();
			final List<ContextParameter> trContextParameters = transformedHybrisProcess.getContextParameter();
			Assert.assertEquals(dContextParameters.size(), trContextParameters.size());

			final Map<String, ContextParameter> dContextParametersMap = dContextParameters.stream()
					.collect(Collectors.toMap(e -> e.getName(), e -> e));
			final Map<String, ContextParameter> trContextParametersMap = trContextParameters.stream()
					.collect(Collectors.toMap(e -> e.getName(), e -> e));

			compareContextParameters(dContextParametersMap, trContextParametersMap);
			return isSame;
		}
	}

	void compareContextParameters(final Map<String, ContextParameter> dContextParametersMap,
			final Map<String, ContextParameter> trContextParametersMap)
	{
		final List<String> missingContextParameters = dContextParametersMap.entrySet().stream()
				.filter(e -> trContextParametersMap.get(e.getKey()) == null).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals("[]", missingContextParameters.toString());

	}

	boolean compare(final Object fromNode, final Object toNode)
	{
		if (fromNode instanceof Action && toNode instanceof Action)
		{
			return compareActions((Action) fromNode, (Action) toNode);
		}

		if (fromNode instanceof Wait && toNode instanceof Wait)
		{
			return compareWait((Wait) fromNode, (Wait) toNode);
		}

		if (fromNode instanceof End && toNode instanceof End)
		{
			return compareEnd((End) fromNode, (End) toNode);
		}

		if (fromNode instanceof ScriptAction && toNode instanceof ScriptAction)
		{
			return compareScriptAction((ScriptAction) fromNode, (ScriptAction) toNode);
		}

		return false;
	}

	boolean compareScriptAction(final ScriptAction fromNode, final ScriptAction toNode)
	{
		Assert.assertEquals(fromNode.getId(), toNode.getId());

		final List<Transition> fromTransitions = fromNode.getTransition();
		final List<Transition> toTransitions = toNode.getTransition();
		Assert.assertEquals(fromTransitions.size(), toTransitions.size());

		boolean found = false;
		for (final Transition fromTransition : fromTransitions)
		{
			found = false;
			for (final Transition toTransition : toTransitions)
			{
				found = findEqualTransition(fromNode.getId(), fromTransition, toTransition);
				if (found)
				{
					break;
				}
			}
			if (!found)
			{
				break;
			}
		}
		final Script fromScript = fromNode.getScript();
		final String fromValue = fromScript.getValue();
		final String fromType = fromScript.getType();

		final Script toScript = toNode.getScript();
		final String toValue = toScript.getValue();
		final String toType = toScript.getType();
		Assert.assertEquals(fromValue, toValue);
		Assert.assertEquals(fromType, toType);

		Assert.assertEquals("Actions with ID " + fromNode.getId() + " did not match", true, found);
		return true;
	}

	boolean compareEnd(final End fromNode, final End toNode)
	{
		return fromNode.getId().equals(toNode.getId()) && fromNode.getState().equals(toNode.getState());
	}

	boolean compareWait(final Wait fromNode, final Wait toNode)
	{
		//boolean isSame = fromNode.getId().equals(toNode.getId());
		//Assert.assertEquals(fromNode.getId(), toNode.getId());//Removing the ID match as this may change in case of wait with timeout
		String fromThen = fromNode.getThen();
		final String toThen = toNode.getThen();
		if (toThen.indexOf("IME") > -1)
		{
			fromThen = fromThen + "IME";
		}
		Assert.assertEquals(fromThen, toThen);

		/*
		 * if(fromThen != null && toThen != null){ isSame = isSame && fromThen.equals(toThen); }else if(fromThen == null
		 * && toThen == null){ isSame = isSame && true; }else{ isSame = false; }
		 */

		final String fromEvent = fromNode.getEvent();
		final String toEvent = toNode.getEvent();
		Assert.assertEquals(fromEvent, toEvent);
		/*
		 * if(fromEvent != null && toEvent != null){ isSame = isSame && fromEvent.equals(toEvent); }else if(fromEvent ==
		 * null && toEvent == null){ isSame = isSame && true; }else{ isSame = false; }
		 */

		final Case fromCaze = fromNode.getCase();
		final Case toCaze = toNode.getCase();
		if (fromCaze != null && toCaze != null)
		{
			return compareCaze(fromCaze, toCaze);
		}
		else if (fromCaze == null && toCaze == null)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	boolean compareCaze(final Case fromCaze, final Case toCaze)
	{
		Assert.assertEquals(fromCaze.getEvent(), toCaze.getEvent());
		//boolean isSame = fromCaze.getEvent().equals(toCaze.getEvent());

		final List<Choice> fromChoices = fromCaze.getChoice();
		final List<Choice> toChoices = toCaze.getChoice();
		Assert.assertEquals(fromChoices.size(), toChoices.size());
		/*
		 * boolean isSame = isSame && (fromChoices.size() == toChoices.size()); if(!isSame){ return false; }
		 */
		boolean found = false;
		for (final Choice fromChoice : fromChoices)
		{
			found = false;
			for (final Choice toChoice : toChoices)
			{
				found = findEqualChoice(fromChoice, toChoice);
				if (found)
				{
					break;
				}
			}
			//isSame = isSame && found;
			if (!found)
			{
				break;
			}
		}
		return found;
	}

	boolean findEqualChoice(final Choice fromChoice, final Choice toChoice)
	{
		return fromChoice.getId().equals(toChoice.getId()) && fromChoice.getThen().equals(toChoice.getThen());
	}

	boolean compareActions(final Action fromNode, final Action toNode)
	{
		Assert.assertEquals(fromNode.getId(), toNode.getId());
		Assert.assertEquals(fromNode.getBean(), toNode.getBean());

		final List<Transition> fromTransitions = fromNode.getTransition();
		final List<Transition> toTransitions = toNode.getTransition();
		Assert.assertEquals(fromTransitions.size(), toTransitions.size());

		boolean found = false;
		for (final Transition fromTransition : fromTransitions)
		{
			found = false;
			for (final Transition toTransition : toTransitions)
			{
				found = findEqualTransition(fromNode.getId(), fromTransition, toTransition);
				if (found)
				{
					break;
				}
			}
			if (!found)
			{
				break;
			}
		}
		Assert.assertEquals("Actions with ID " + fromNode.getId() + " did not match", true, found);
		return true;
	}

	boolean findEqualTransition(final String actionId, final Transition fromTransition, final Transition toTransition)
	{
		if (fromTransition.getName().equals(toTransition.getName()))
		{
			String expectedTo = fromTransition.getTo();
			String actualTo = toTransition.getTo();
			if (actualTo.equals(expectedTo))
			{
				return true;
			}
			else if (actualTo.equals(expectedTo + "IME"))
			{//Check if it ends with IME..in case this is for Wait w/ timeout
				return true;
			}
		}

		return false;
	}

	Map<String, Object> createNodeLookup(final Process process)
	{
		final Map<String, Object> nodeLookup = new HashMap<String, Object>();
		final List<Object> nodes = process.getNodes();
		for (final Object node : nodes)
		{
			String id = null;
			if (node instanceof Action)
			{
				final Action action = (Action) node;
				id = action.getId();
				nodeLookup.put(id, action);
			}
			else if (node instanceof Wait)
			{
				final Wait wait = (Wait) node;
				id = wait.getId();
				nodeLookup.put(id, wait);
			}
			else if (node instanceof End)
			{
				final End end = (End) node;
				id = end.getId();
				nodeLookup.put(id, end);
			}
			else if (node instanceof ScriptAction)
			{
				final ScriptAction scriptAction = (ScriptAction) node;
				id = scriptAction.getId();
				nodeLookup.put(id, scriptAction);
			}
			else
			{
				throw new IllegalArgumentException("Unkown object encountered");
			}
		}
		return nodeLookup;
	}
}

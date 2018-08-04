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
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import reactor.util.CollectionUtils;

public class CPQHomeTheaterPocConfigMockImpl extends BaseRunTimeConfigMockImpl
{
	private static final String INSTANCE_ID = "1";
	public static final String KB_ID = "24";
	public static final String CONFIG_NAME = "Config Name";
	public static final String ROOT_INSTANCE_ID = INSTANCE_ID;
	public static final String ROOT_INSTANCE_NAME = "CPQ_HOME_THEATER";
	public static final String ROOT_INSTANCE_LANG_DEP_NAME = "Laptop Professional Plus";
	private static final String RECEIVER_SUBINSTANCE_ID = "2";
	public static final String RECEIVER_SUBINSTANCE_NAME = "CPQ_RECEIVER";
	public static final String RECEIVER_SUBINSTANCE_LANG_DEP_NAME = "Receiver";
	private static final String FRONT_SPEAKERS_SUBINSTANCE_ID = "3";
	private static final String FRONT_SPEAKERS_SUBINSTANCE_NAME = "CPQ_FRONT_SPEAKERS";
	private static final String FRONT_SPEAKERS_SUBINSTANCE_LANG_DE_NAME = "Front Speakers";
	private static final String REAR_SPEAKERS_SUBINSTANCE_ID = "4";
	private static final String REAR_SPEAKERS_SUBINSTANCE_NAME = "CPQ_REAR_SPEAKERS";
	private static final String REAR_SPEAKERS_SUBINSTANCE_LANG_DEP_NAME = "Rear Speakers";
	private static final String SUBWOOFER_SUBINSTANCE_ID = "5";
	private static final String SUBWOOFER_SUBINSTANCE_NAME = "CPQ_SUBWOOFER";
	private static final String SUBWOOFER_SUBINSTANCE_LANG_DEP_NAME = "Subwoofer";
	private static final String BLU_RAY_SUBINSTANCE_ID = "6";
	private static final String BLU_RAY_SUBINSTANCE_NAME = "CPQ_BLU_RAY_PLAYER";
	private static final String BLU_RAY_SUBINSTANCE_LANG_DEP_NAME = "Blu-Ray Player";

	//Cstics:
	//CPQ_HT_SURROUND_MODE
	// example for an id with a space inside
	private static final String CPQ_HT_SURROUND_MODE = "CPQ_HT_SURROUND MODE";
	private static final String LANG_DEP_NAME_CPQ_HT_SURROUND_MODE = "Surround Mode";
	private static final String STEREO = "STEREO";
	private static final String LANG_DEP_NAME_STEREO = "Stereo";
	private static final String SURROUND = "SURROUND";
	private static final String LANG_DEP_NAME_SURROND = "Multichannel Surrond Sound";

	//CPQ_HT_INCLUDE_TV
	private static final String CPQ_HT_INCLUDE_TV = "CPQ_HT_INCLUDE_TV";
	private static final String LANG_DEP_NAME_INCLUDE_TV = "Include TV?";
	private static final String X = "X";

	//CPQ_HT_INCLUDE_BR
	private static final String CPQ_HT_INCLUDE_BR = "CPQ_HT_INCLUDE_BR";
	private static final String LANG_DEP_NAME_CPQ_HT_INCLUDE_BR = "Include Blu-Ray Player?";

	//CPQ_HT_VIDEO_SOURCES
	private static final String CPQ_HT_VIDEO_SOURCES = "CPQ_HT_VIDEO_SOURCES";
	private static final String LANG_DEP_NAME_VIDEO_SOURCES = "Video Sources";
	private static final String ATV = "ATV";
	private static final String LANG_DEP_NAME_ATV = "Apple TV";
	private static final String NTW = "NTW";
	private static final String LANG_DEP_NAME_NTW = "Nintendo Wii U";
	private static final String XB1 = "XB1";
	private static final String LANG_DEP_NAME_XB1 = "Microsoft Xbox One";
	private static final String PS4 = "PS4";
	private static final String LANG_DEP_NAME_PS4 = "Sony Playstation 4";
	private static final String GGC = "GGC";
	private static final String LANG_DEP_NAME_GGC = "Google Chromecast";
	private static final String AFT = "AFT";
	private static final String LANG_DEP_NAME_AFT = "Amazon Fire TV";

	//CPQ_HT_SUBWOOFER
	private static final String CPQ_HT_SUBWOOFER = "CPQ_HT_SUBWOOFER";
	private static final String LANG_DEP_NAME_INCLUDE_SUBWOOFER = "Include Subwoofer?";

	//CPQ_HT_POWER2
	private static final String CPQ_HT_POWER2 = "CPQ_HT_POWER2";
	private static final String LANG_DEP_NAME_CPQ_HT_POWER2 = "Dynamic Power per Channel";
	private static final String CPQ_HT_POWER2_161_OO = "161_OO";
	private static final String LANG_DEP_NAME_OVER_160_W = "Over 160 W";
	private static final String CPQ_HT_POWER2_121_160 = "121_160";
	private static final String LANG_DEP_NAME_121_TO_160_W = "121 to 160 W";
	private static final String CPQ_HT_POWER2_0_120 = "0_120";
	private static final String LANG_DEP_NAME_UP_TO_120_W = "Up to 120 W";

	//CPQ_HT_RECV_MODEL2
	private static final String CPQ_HT_RECV_MODEL2 = "CPQ_HT_RECV_MODEL2";
	private static final String LANG_DEP_NAME_CPQ_HT_RECV_MODEL2 = "Receiver Model";
	private static final String MA_SR5010 = "MA_SR5010";
	private static final String LANG_DEP_NAME_MA_SR5010 = "Marantz SR5010";
	private static final String DN_520 = "DN_520";
	private static final String LANG_DEP_NAME_DN_520 = "Denon PME-520AE";
	private static final String DN_1520 = "DN_1520";
	private static final String LANG_DEP_NAME_DN_1520 = "Denon PMA-1520AE";
	private static final String AVM_62 = "AVM_62";
	private static final String LANG_DEP_NAME_AVM_62 = "AVM Ovation 6.2";
	private static final String OK_NR656 = "OK_NR656";
	private static final String LANG_DEP_NAME_OK_NR656 = "Onkyo TX-NR656";
	private static final String OK_RC630 = "OK_RC630";
	private static final String LANG_DEP_NAME_OK_RC630 = "Onkyo HT-RC630";
	private static final String YM_V379 = "YM_V379";
	private static final String LANG_DEP_NAME_YM_V379 = "Yamaha RX-V379";
	private static final String YM_A750 = "YM_A750";
	private static final String LANG_DEP_NAME_YM_A750 = "Yamaha A750";

	//CPQ_HT_RECV_MODE
	private static final String CPQ_HT_RECV_MODE = "CPQ_HT_RECV_MODE";
	private static final String LANG_DEP_NAME_CPQ_HT_RECV_MODE = "Receiver Mode";
	private static final String STE = "STE";
	private static final String SUR = "SUR";
	private static final String LANG_DEP_NAME_SUR = "Surround";

	//CPQ_HT_BR_4K
	private static final String CPQ_HT_BR_4K = "CPQ_HT_BR_4K";
	private static final String LANG_DEP_NAME_CPQ_HT_BR_4K = "4K compatible?";
	private static final String Y = "Y";
	private static final String LANG_DEP_NAME_Y = "Yes";
	private static final String N = "N";
	private static final String LANG_DEP_NAME_N = "No";

	//CPQ_HT_BR_MODEL
	private static final String CPQ_HT_BR_MODEL = "CPQ_HT_BR_MODEL";
	private static final String LANG_DEP_NAME_CPQ_HT_BR_MODEL = "Blu-Ray Player Model";
	private static final String SN_1700 = "SN_1700";
	private static final String LANG_DEP_NAME_SN_1700 = "Sony BDP-S1700";
	private static final String SN_6700 = "SN_6700";
	private static final String LANG_DEP_NAME_SN_6700 = "Sony BDP-S6700";

	//CPQ_HT_SW_MODEL
	private static final String CPQ_HT_SW_MODEL = "CPQ_HT_SW_MODEL";
	private static final String LANG_DEP_NAME_CPQ_HT_SW_MODEL = "Subwoofer Model";
	private static final String CT_62 = "CT_62";
	private static final String LANG_DEP_NAME_CT_62 = "Canton SUB 6.2";
	private static final String CT_900 = "CT_900";
	private static final String LANG_DEP_NAME_CT_900 = "Canton SUB 900";
	private static final String CT_12_2 = "CT_12_2";
	private static final String LANG_DEP_NAME_CT_12_2 = "Canton  SUB 12.2";

	//CPQ_HT_SW_POWER
	private static final String CPQ_HT_SW_POWER = "CPQ_HT_SW_POWER";
	private static final String LANG_DEP_NAME_CPQ_HT_SW_POWER = "Power output";

	//CPQ_HT_SPK_INCLUDE_STAND
	private static final String CPQ_HT_SPK_INCLUDE_STAND = "CPQ_HT_SPK_INCLUDE_STAND";
	private static final String LANG_DEP_NAME_CPQ_HT_SPK_INCLUDE_STAND = "Include Stand";

	//CPQ_HT_SPK_INCLUDE_CABLE
	private static final String CPQ_HT_SPK_INCLUDE_CABLE = "CPQ_HT_SPK_INCLUDE_CABLE";
	private static final String LANG_DEP_NAME_CPQ_HT_SPK_INCLUDE_CABLE = "Include Cable?";

	//CPQ_HT_SPK_POWER
	private static final String CPQ_HT_SPK_POWER = "CPQ_HT_SPK_POWER";
	private static final String LANG_DEP_NAME_CPQ_HT_SPK_POWER = "Maximum Power Input";

	//CPQ_HT_SPK_COLOR
	private static final String CPQ_HT_SPK_COLOR = "CPQ_HT_SPK_COLOR";
	private static final String LANG_DEP_NAME_CPQ_HT_SPK_COLOR = "Speaker Color";
	private static final String BLK = "BLK";
	private static final String LANG_DEP_NAME_BLK = "Black Gloss";
	private static final String WHT = "WHT";
	private static final String LANG_DEP_NAME_WHT = "White Gloss";
	private static final String OAK = "OAK";
	private static final String LANG_DEP_NAME_OAK = "Oak Wood";
	private static final String CHR = "CHR";
	private static final String LANG_DEP_NAME_CHR = "Cherry Wood";
	private static final String RED = "RED";
	private static final String LANG_DEP_NAME_RED = "Red";

	//CPQ_HT_SPK_MODEL
	private static final String CPQ_HT_SPK_MODEL = "CPQ_HT_SPK_MODEL";
	private static final String LANG_DEP_NAME_CPQ_HT_SPK_MODEL = "Speaker Model";
	private static final String QD_TITAN_VIII = "QD_TITAN_VIII";
	private static final String LANG_DEP_NAME_QD_TITAN_VIII = "Quadral Titan VIII";
	private static final String CT_ERGO_620 = "CT_ERGO_620";
	private static final String LANG_DEP_NAME_CT_ERGO_620 = "Canton Ergo 620";
	private static final String YM_NS_B700 = "YM_NS_B700";
	private static final String LANG_DEP_NAME_YM_NS_B700 = "Yamaha NS-B700";
	private static final String YM_NS_F160 = "YM_NS_F160";
	private static final String LANG_DEP_NAME_YM_NS_F160 = "Yamaha NS-F160";

	@Override
	public ConfigModel createDefaultConfiguration()
	{

		// Model
		final ConfigModel model = createDefaultConfigModel("Configuration for CPQ_HOME_THEATER " + getConfigId(), false);
		model.setKbId(KB_ID);

		// root instance
		final InstanceModel rootInstance = createDefaultRootInstance(model, ROOT_INSTANCE_NAME, ROOT_INSTANCE_LANG_DEP_NAME);

		// cstic groups:
		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		addCsticGroup(csticGroups, InstanceModel.GENERAL_GROUP_NAME, "");
		addCsticGroup(csticGroups, "1", "Powerful audio", CPQ_HT_SURROUND_MODE, CPQ_HT_SUBWOOFER, CPQ_HT_POWER2);
		addCsticGroup(csticGroups, "2", "Visual entertainment", CPQ_HT_INCLUDE_TV, CPQ_HT_INCLUDE_BR, CPQ_HT_VIDEO_SOURCES);
		rootInstance.setCsticGroups(csticGroups);

		// cstics and values:
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createSurroundModeCstic(INSTANCE_ID));
		cstics.add(createIncludeTVCstic(INSTANCE_ID));
		cstics.add(createIncludeBluRayCstic(INSTANCE_ID));
		cstics.add(createVideoSourcesCstic(INSTANCE_ID));
		cstics.add(createIncludeSubwooferCstic(INSTANCE_ID));
		cstics.add(createPower2Cstic(INSTANCE_ID));
		rootInstance.setCstics(cstics);

		// 'Receiver' subInstance
		final InstanceModel subInstanceReceiver = createSubInstance(RECEIVER_SUBINSTANCE_ID, RECEIVER_SUBINSTANCE_NAME,
				RECEIVER_SUBINSTANCE_LANG_DEP_NAME);
		rootInstance.getSubInstances().add(subInstanceReceiver);

		// cstic groups for 'Receiver' subInstance:
		final List<CsticGroupModel> receiverSubInstanceCsticGroups = new ArrayList<>();
		addCsticGroup(receiverSubInstanceCsticGroups, InstanceModel.GENERAL_GROUP_NAME, "", CPQ_HT_RECV_MODEL2, CPQ_HT_RECV_MODE,
				CPQ_HT_POWER2);
		subInstanceReceiver.setCsticGroups(receiverSubInstanceCsticGroups);

		// cstics and values for 'Receiver' subInstance:
		final List<CsticModel> subInstanceReceiverCstics = new ArrayList<>();
		subInstanceReceiverCstics.add(createReceiveModelCstic(RECEIVER_SUBINSTANCE_ID));
		subInstanceReceiverCstics.add(createReceiveModeCstic(RECEIVER_SUBINSTANCE_ID));
		subInstanceReceiverCstics.add(createPower2Cstic(RECEIVER_SUBINSTANCE_ID));
		subInstanceReceiver.setCstics(subInstanceReceiverCstics);

		return model;
	}

	protected CsticModel createSpeakerIncludeStandCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SPK_INCLUDE_STAND, LANG_DEP_NAME_CPQ_HT_SPK_INCLUDE_STAND);
		builder.withLongText("Include a stand with this speaker");
		builder.simpleFlag();
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createSpeakerIncludeCableCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SPK_INCLUDE_CABLE, LANG_DEP_NAME_CPQ_HT_SPK_INCLUDE_CABLE);
		builder.withLongText("Should we include cables for your speakers?");
		builder.simpleFlag();
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createSpeakerPowerCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SPK_POWER, LANG_DEP_NAME_CPQ_HT_SPK_POWER);
		builder.numericType(0, 3);
		builder.withDefaultUIState().readOnly();
		return builder.build();
	}

	protected CsticModel createSpeakerColorCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SPK_COLOR, LANG_DEP_NAME_CPQ_HT_SPK_COLOR).withLongText("The color of the speaker");
		builder.stringType().singleSelection();
		builder.addOption(BLK, LANG_DEP_NAME_BLK).addOption(WHT, LANG_DEP_NAME_WHT).addOption(OAK, LANG_DEP_NAME_OAK)
				.addOption(CHR, LANG_DEP_NAME_CHR).addOption(RED, LANG_DEP_NAME_RED);
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createSpeakerModelCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SPK_MODEL, LANG_DEP_NAME_CPQ_HT_SPK_MODEL);
		builder.stringType().singleSelection();
		builder.addOption(YM_NS_F160, LANG_DEP_NAME_YM_NS_F160).addOption(YM_NS_B700, LANG_DEP_NAME_YM_NS_B700)
				.addOption(CT_ERGO_620, LANG_DEP_NAME_CT_ERGO_620).addOption(QD_TITAN_VIII, LANG_DEP_NAME_QD_TITAN_VIII);
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createSubwooferPowerCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SW_POWER, LANG_DEP_NAME_CPQ_HT_SW_POWER);
		builder.withLongText("The power output of this subwoofer");
		builder.numericType(0, 3);
		builder.withDefaultUIState().readOnly();
		return builder.build();
	}

	protected CsticModel createSubwooferModelCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SW_MODEL, LANG_DEP_NAME_CPQ_HT_SW_MODEL);
		builder.stringType().singleSelection();
		builder.addOption(CT_12_2, LANG_DEP_NAME_CT_12_2).addOption(CT_900, LANG_DEP_NAME_CT_900).addOption(CT_62,
				LANG_DEP_NAME_CT_62);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createBluRay4KCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_BR_4K, LANG_DEP_NAME_CPQ_HT_BR_4K);
		builder.withLongText("Is this a 4K compatible Blu-Ray Player?");
		builder.stringType().singleSelection();
		builder.addSelectedOption(Y, LANG_DEP_NAME_Y).addOption(N, LANG_DEP_NAME_N);
		builder.withDefaultUIState().readOnly();
		return builder.build();
	}

	protected CsticModel createBluRayModelCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_BR_MODEL, LANG_DEP_NAME_CPQ_HT_BR_MODEL);
		builder.withLongText("Which Blu-Ray Player to include");
		builder.stringType().singleSelection();
		builder.addOption(SN_1700, LANG_DEP_NAME_SN_1700).addSelectedOption(SN_6700, LANG_DEP_NAME_SN_6700);
		builder.withDefaultUIState().required();
		return builder.build();
	}


	protected CsticModel createReceiveModeCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_RECV_MODE, LANG_DEP_NAME_CPQ_HT_RECV_MODE);
		builder.withLongText("Is this a stereo or surround receiver");
		builder.stringType().singleSelection();
		builder.addOption(STE, LANG_DEP_NAME_STEREO).addOption(SUR, LANG_DEP_NAME_SUR);
		builder.withDefaultUIState().readOnly();
		return builder.build();
	}

	protected CsticModel createReceiveModelCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_RECV_MODEL2, LANG_DEP_NAME_CPQ_HT_RECV_MODEL2);
		builder.withLongText("Which receiver to include?");
		builder.stringType().singleSelection();
		builder.addOption(YM_A750, LANG_DEP_NAME_YM_A750).addOption(YM_V379, LANG_DEP_NAME_YM_V379)
				.addOption(OK_RC630, LANG_DEP_NAME_OK_RC630).addOption(OK_NR656, LANG_DEP_NAME_OK_NR656)
				.addOption(AVM_62, LANG_DEP_NAME_AVM_62).addOption(DN_1520, LANG_DEP_NAME_DN_1520)
				.addOption(DN_520, LANG_DEP_NAME_DN_520).addOption(MA_SR5010, LANG_DEP_NAME_MA_SR5010);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected InstanceModel createSubInstance(final String instanceId, final String name, final String langDepName)
	{
		final InstanceModel subInstance = createInstance();
		subInstance.setId(instanceId);
		subInstance.setName(name);
		subInstance.setLanguageDependentName(langDepName);
		subInstance.setRootInstance(false);
		subInstance.setComplete(true);
		subInstance.setConsistent(true);
		subInstance.setSubInstances(Collections.emptyList());

		return subInstance;
	}

	protected CsticModel createPower2Cstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_POWER2, LANG_DEP_NAME_CPQ_HT_POWER2);
		builder.withLongText("How much output power should the amplifier have per channel?");
		builder.stringType().singleSelection();
		builder.addOption(CPQ_HT_POWER2_0_120, LANG_DEP_NAME_UP_TO_120_W)
				.addOption(CPQ_HT_POWER2_121_160, LANG_DEP_NAME_121_TO_160_W)
				.addOption(CPQ_HT_POWER2_161_OO, LANG_DEP_NAME_OVER_160_W);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createIncludeSubwooferCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SUBWOOFER, LANG_DEP_NAME_INCLUDE_SUBWOOFER);
		builder.withLongText("Whether to include a subwoofer for extra deep bass");
		builder.simpleFlag();
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createVideoSourcesCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_VIDEO_SOURCES, LANG_DEP_NAME_VIDEO_SOURCES);
		builder.withLongText("Which video sources to include");
		builder.stringType().multiSelection();
		builder.addOption(ATV, LANG_DEP_NAME_ATV).addOption(AFT, LANG_DEP_NAME_AFT).addOption(GGC, LANG_DEP_NAME_GGC)
				.addOption(PS4, LANG_DEP_NAME_PS4).addOption(XB1, LANG_DEP_NAME_XB1).addOption(NTW, LANG_DEP_NAME_NTW);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createIncludeBluRayCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_INCLUDE_BR, LANG_DEP_NAME_CPQ_HT_INCLUDE_BR);
		builder.withLongText("Select to include Blu-Ray Player in your Home Theater");
		builder.simpleFlag();
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createIncludeTVCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_INCLUDE_TV, LANG_DEP_NAME_INCLUDE_TV);
		builder.withLongText("Select to include a TV");
		builder.simpleFlag();
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createSurroundModeCstic(final String instanceId)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(instanceId, null);
		builder.withName(CPQ_HT_SURROUND_MODE, LANG_DEP_NAME_CPQ_HT_SURROUND_MODE);
		builder.withLongText("Surround Mode to support - will influence speaker setup");
		builder.stringType().singleSelection();
		builder.addOption(STEREO, LANG_DEP_NAME_STEREO).addOption(SURROUND, LANG_DEP_NAME_SURROND);
		builder.withDefaultUIState().required();
		return builder.build();
	}

	@Override
	public void checkCstic(final ConfigModel model, final InstanceModel instance, final CsticModel cstic)
	{
		cstic.setComplete(true);
		cstic.setConsistent(true);

		final String name = cstic.getName();
		if (name.equals(CPQ_HT_SURROUND_MODE))
		{
			checkSurrondMode(cstic, model);
		}
		if (name.equals(CPQ_HT_SUBWOOFER))
		{
			checkSubwooferMode(cstic, model);
		}
		if (name.equals(CPQ_HT_INCLUDE_BR))
		{
			checkBluRay(cstic, model);
		}
	}

	protected void checkBluRay(final CsticModel cstic, final ConfigModel model)
	{
		final InstanceModel instance = model.getRootInstance();

		final List<CsticValueModel> assignedValues = cstic.getAssignedValues();
		if (CollectionUtils.isEmpty(assignedValues))
		{
			instance.removeSubInstance(BLU_RAY_SUBINSTANCE_ID);
			return;
		}

		final String value = assignedValues.get(0).getName();

		if (X.equals(value))
		{
			addBlueRay(instance);
		}
	}

	protected void addBlueRay(final InstanceModel instance)
	{
		final InstanceModel subInstance = instance.getSubInstance(BLU_RAY_SUBINSTANCE_ID);
		if (null == subInstance)
		{
			// 'Blu-Ray Player' subInstance
			final InstanceModel subInstanceBluRay = createSubInstance(BLU_RAY_SUBINSTANCE_ID, BLU_RAY_SUBINSTANCE_NAME,
					BLU_RAY_SUBINSTANCE_LANG_DEP_NAME);
			instance.getSubInstances().add(subInstanceBluRay);

			// cstic groups for 'Blu-Ray Player' subInstance:
			final List<CsticGroupModel> subInstanceBluRayCsticGroups = new ArrayList<>();
			addCsticGroup(subInstanceBluRayCsticGroups, InstanceModel.GENERAL_GROUP_NAME, "", CPQ_HT_BR_MODEL, CPQ_HT_BR_4K);
			subInstanceBluRay.setCsticGroups(subInstanceBluRayCsticGroups);

			// cstics and values for 'Blu-Ray Player' subInstance:
			final List<CsticModel> subInstanceBluRayCstics = new ArrayList<>();
			subInstanceBluRayCstics.add(createBluRayModelCstic(BLU_RAY_SUBINSTANCE_ID));
			subInstanceBluRayCstics.add(createBluRay4KCstic(BLU_RAY_SUBINSTANCE_ID));
			subInstanceBluRay.setCstics(subInstanceBluRayCstics);
		}
	}

	protected void checkSubwooferMode(final CsticModel cstic, final ConfigModel model)
	{
		final InstanceModel instance = model.getRootInstance();

		final List<CsticValueModel> assignedValues = cstic.getAssignedValues();
		if (CollectionUtils.isEmpty(assignedValues))
		{
			instance.removeSubInstance(SUBWOOFER_SUBINSTANCE_ID);
			return;
		}

		final String value = assignedValues.get(0).getName();

		if (X.equals(value))
		{
			addSubwoofer(instance);
		}
	}

	protected void addSubwoofer(final InstanceModel instance)
	{
		final InstanceModel subInstance = instance.getSubInstance(SUBWOOFER_SUBINSTANCE_ID);
		if (null == subInstance)
		{
			//'Subwoofer' subInstance
			final InstanceModel subInstanceSubwoofer = createSubInstance(SUBWOOFER_SUBINSTANCE_ID, SUBWOOFER_SUBINSTANCE_NAME,
					SUBWOOFER_SUBINSTANCE_LANG_DEP_NAME);
			instance.getSubInstances().add(subInstanceSubwoofer);

			// cstic groups for 'Subwoofer' subInstance:
			final List<CsticGroupModel> subInstanceSubwooferCsticGroups = new ArrayList<>();
			addCsticGroup(subInstanceSubwooferCsticGroups, InstanceModel.GENERAL_GROUP_NAME, "", CPQ_HT_SW_MODEL, CPQ_HT_SW_POWER);
			subInstanceSubwoofer.setCsticGroups(subInstanceSubwooferCsticGroups);

			// cstics and values for 'Subwoofer' subInstance:
			final List<CsticModel> subInstanceSubwooferCstics = new ArrayList<>();
			subInstanceSubwooferCstics.add(createSubwooferModelCstic(SUBWOOFER_SUBINSTANCE_ID));
			subInstanceSubwooferCstics.add(createSubwooferPowerCstic(SUBWOOFER_SUBINSTANCE_ID));
			subInstanceSubwoofer.setCstics(subInstanceSubwooferCstics);
		}
	}

	protected void checkSurrondMode(final CsticModel cstic, final ConfigModel model)
	{
		final InstanceModel instance = model.getRootInstance();
		final List<CsticValueModel> assignedValues = cstic.getAssignedValues();
		if (CollectionUtils.isEmpty(assignedValues))
		{
			return;
		}

		final String value = assignedValues.get(0).getName();

		if (STEREO.equals(value))
		{
			instance.removeSubInstance(REAR_SPEAKERS_SUBINSTANCE_ID);
			addFrontSpeakers(instance);
		}
		else if (SURROUND.equals(value))
		{
			addFrontSpeakers(instance);
			addRearSpeakers(instance);
		}
	}

	protected void addRearSpeakers(final InstanceModel instance)
	{
		final InstanceModel subInstance = instance.getSubInstance(REAR_SPEAKERS_SUBINSTANCE_ID);
		if (null == subInstance)
		{
			// 'Rear Speakers' subInstance
			final InstanceModel subInstanceRearSpeaker = createSubInstance(REAR_SPEAKERS_SUBINSTANCE_ID,
					REAR_SPEAKERS_SUBINSTANCE_NAME, REAR_SPEAKERS_SUBINSTANCE_LANG_DEP_NAME);
			instance.getSubInstances().add(subInstanceRearSpeaker);
			fillSubInstanceSpeaker(subInstanceRearSpeaker, REAR_SPEAKERS_SUBINSTANCE_ID);
		}
	}

	protected void addFrontSpeakers(final InstanceModel instance)
	{
		final InstanceModel subInstance = instance.getSubInstance(FRONT_SPEAKERS_SUBINSTANCE_ID);
		if (null == subInstance)
		{
			// 'Front Speakers' subInstance
			final InstanceModel subInstanceFrontSpeaker = createSubInstance(FRONT_SPEAKERS_SUBINSTANCE_ID,
					FRONT_SPEAKERS_SUBINSTANCE_NAME, FRONT_SPEAKERS_SUBINSTANCE_LANG_DE_NAME);
			instance.getSubInstances().add(subInstanceFrontSpeaker);
			fillSubInstanceSpeaker(subInstanceFrontSpeaker, FRONT_SPEAKERS_SUBINSTANCE_ID);
		}
	}

	private void fillSubInstanceSpeaker(InstanceModel subInstanceFrontSpeaker, String speakerInstanceId) {
		// cstic groups for 'Front Speakers' subInstance:
		final List<CsticGroupModel> subInstanceFrontSpeakerCsticGroups = new ArrayList<>();
		addCsticGroup(subInstanceFrontSpeakerCsticGroups, InstanceModel.GENERAL_GROUP_NAME, "", CPQ_HT_SPK_MODEL,
                CPQ_HT_SPK_COLOR, CPQ_HT_SPK_POWER, CPQ_HT_SPK_INCLUDE_CABLE, CPQ_HT_SPK_INCLUDE_STAND);
		subInstanceFrontSpeaker.setCsticGroups(subInstanceFrontSpeakerCsticGroups);

		// cstics and values for 'Front Speakers' subInstance:
		final List<CsticModel> subInstanceFrontSpeakerCstics = new ArrayList<>();
		subInstanceFrontSpeakerCstics.add(createSpeakerModelCstic(speakerInstanceId));
		subInstanceFrontSpeakerCstics.add(createSpeakerColorCstic(speakerInstanceId));
		subInstanceFrontSpeakerCstics.add(createSpeakerPowerCstic(speakerInstanceId));
		subInstanceFrontSpeakerCstics.add(createSpeakerIncludeCableCstic(speakerInstanceId));
		subInstanceFrontSpeakerCstics.add(createSpeakerIncludeStandCstic(speakerInstanceId));
		subInstanceFrontSpeaker.setCstics(subInstanceFrontSpeakerCstics);
	}
}

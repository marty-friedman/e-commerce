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
import {IWaitDialogService} from 'smarteditcommons';

/** @internal */
/* @ngInject */
export class WaitDialogService extends IWaitDialogService {

	/** @internal */
	public gatewayId: string = "WaitDialogService";
	private modalService: any;
	private modalManager: any;

	constructor(gatewayProxy: any, modalService: any) {
		super();
		gatewayProxy.initForService(this);
		this.modalService = modalService;
		this.modalManager = null;
	}

	showWaitModal(customLoadingMessageLocalizedKey: string): void | angular.IPromise<void> {
		if (this.modalManager == null) {
			return this.modalService.open({
				templateUrl: 'waitDialog.html',
				cssClasses: "ySEWaitDialog",
				controller: ['modalManager', ((modalManager: any) => {
					modalManager.loadingMessage = (customLoadingMessageLocalizedKey) ? customLoadingMessageLocalizedKey : "se.wait.dialog.message";
					this.modalManager = modalManager;
				})]
			});
		}
	}

	hideWaitModal(): void {
		if (this.modalManager != null) {
			this.modalManager.close();
			this.modalManager = null;
		}
	}
}

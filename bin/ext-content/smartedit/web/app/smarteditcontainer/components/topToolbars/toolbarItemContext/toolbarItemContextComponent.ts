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
/* @ngInject */
class ToolbarItemContextController implements angular.IController {

	public displayContext: boolean = true;

	private itemKey: string;
	private unregShowContext: any;
	private unregHideContext: any;

	constructor(
		private crossFrameEventService: any,
		private SHOW_TOOLBAR_ITEM_CONTEXT: string,
		private HIDE_TOOLBAR_ITEM_CONTEXT: string) {}

	$onInit(): void {

		this.unregShowContext = this.crossFrameEventService.subscribe(this.SHOW_TOOLBAR_ITEM_CONTEXT, (eventId: string, itemKey: string) => {
			if (itemKey === this.itemKey) {
				this.showContext(true);
			}
		});

		this.unregHideContext = this.crossFrameEventService.subscribe(this.HIDE_TOOLBAR_ITEM_CONTEXT, (eventId: string, itemKey: string) => {
			if (itemKey === this.itemKey) {
				this.showContext(false);
			}
		});

	}

	$onDestroy(): void {
		this.unregShowContext();
		this.unregHideContext();
	}

	showContext(show: boolean): void {
		this.displayContext = show;
	}

}

/**
 * Component toolbarItemContext that is responsible for displaying the context for the selected item.
 */
export const toolbarItemContext: angular.IComponentOptions = {
	controller: ToolbarItemContextController,
	controllerAs: '$ctrl',
	templateUrl: 'toolbarItemContextTemplate.html',
	bindings: {
		itemKey: '<',
		contextTemplateUrl: '<',
		itemIsOpen: '<'
	}
};

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
/* jshint unused:false, undef:false */
angular
    .module('navigationNodeMocks', ['ngMockE2E', 'yLoDashModule'])
    .run(
        function($httpBackend, parseQuery, lodash) {

            //navigation entries

            var entries = [{
                itemId: "HomepageNavLink",
                itemType: "CMSLinkComponent",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "1",
                uid: "1"
            }, {
                itemId: "69SlamLink",
                itemType: "CMSParagraphComponent",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "1",
                uid: "2"
            }, {
                itemId: "DakineLink",
                itemType: "CMSLinkComponent",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "1",
                uid: "6"
            }, {
                itemId: "Item-ID-4.1",
                itemType: "ItemType 4.1",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "4",
                uid: "3",
                name: "Entry 4.1",
            }, {
                itemId: "Item-ID-4.2",
                itemType: "ItemType 4.2",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "4",
                uid: "4"
            }, {
                itemId: "Item-ID-6.1",
                itemType: "ItemType 6.1",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "6",
                uid: "5"
            }, {
                itemId: "Item-ID-8.1",
                itemType: "CMSLinkComponent",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "8",
                uid: "6"
            }, {
                itemId: "Item-ID-8.2",
                itemType: "CMSParagraphComponent",
                itemSuperType: "AbstractCMSComponent",
                navigationNodeUid: "8",
                uid: "7"
            }];

            // navigation nodes

            var nodes = [{
                uid: "1",
                name: "node1",
                title: {
                    en: "node1_en",
                    fr: "node1_fr"
                },
                parentUid: "root",
                position: 0,
                hasChildren: true,
                hasEntries: true
            }, {
                uid: "2",
                name: "node2",
                title: {
                    en: "node2_en",
                    fr: "node2_fr"
                },
                parentUid: "root",
                position: 1,
                hasChildren: true,
                hasEntries: false
            }, {
                uid: "4",
                name: "node4",
                title: {
                    "en": "node4_en",
                    "fr": "node4_fr"
                },
                parentUid: "1",
                position: 0,
                hasChildren: true,
                hasEntries: true
            }, {
                uid: "5",
                name: "node5",
                title: {
                    "en": "node5_en",
                    "fr": "node5_fr"
                },
                parentUid: "1",
                position: 1,
                hasChildren: false,
                hasEntries: false
            }, {
                uid: "6",
                name: "node6",
                title: {
                    "en": "node6_en",
                    "fr": "node6_fr"
                },
                parentUid: "2",
                position: 0,
                hasChildren: false,
                hasEntries: false
            }, {
                uid: "7",
                name: "node7",
                title: {
                    "en": "node7_en",
                    "fr": "node7_fr"
                },
                parentUid: "1",
                position: 2,
                hasChildren: false,
                hasEntries: false
            }, {
                uid: "8",
                name: "node8",
                title: {
                    "en": "node8_en",
                    "fr": "node8_fr"
                },
                parentUid: "4",
                position: 0,
                hasChildren: true,
                hasEntries: true
            }, {
                uid: "9",
                name: "node9",
                title: {
                    "en": "node9_en",
                    "fr": "node9_fr"
                },
                parentUid: "8",
                position: 0,
                hasChildren: false,
                hasEntries: false
            }];

            var fetchAncestors = function(parentUid) {
                var parent = nodes.find(function(element) {
                    return element.uid === parentUid;
                });
                if (parent) {
                    return [parent].concat(fetchAncestors(parent.parentUid));
                } else {
                    return [];
                }
            };

            $httpBackend.whenGET(/sites\/.*\/catalogs\/.*\/versions\/.*\/navigations\/([^\/]+)/).respond(function(method, url, data, headers) {

                var uid = /sites\/.*\/catalogs\/.*\/versions\/.*\/navigations\/(.+)/.exec(url)[1];
                var _nodes = lodash.cloneDeep(nodes);
                var node = _nodes.filter(function(element) {
                    return element.uid === uid;
                })[0];

                appendEntriesToNode(node);

                return [200, node];
            });

            $httpBackend.whenGET(/sites\/.*\/catalogs\/.*\/versions\/.*\/navigations/).respond(function(method, url, data, headers) {

                var query = parseQuery(url);
                var parentUID = query.parentUid;
                var uid = query.ancestorTrailFrom;
                var _nodes = lodash.cloneDeep(nodes);

                if (parentUID && !uid) {
                    return [200, {
                        navigationNodes: _nodes.filter(function(node) {
                                return node.parentUid === parentUID;
                            })
                            .map(function(node) {
                                return appendEntriesToNode(node);
                            })
                            .sort(function(a, b) {
                                return a.position - b.position;
                            })
                    }];
                } else if (!parentUID && uid) {
                    return [200, {
                        breadcrumb: fetchAncestors(uid)
                    }];
                }

            });


            $httpBackend.whenPUT(/sites\/.*\/catalogs\/.*\/versions\/.*\/navigations/).respond(function(method, url, data, headers) {

                var payload = JSON.parse(data);

                if (payload.name.indexOf('entriesErrors') > -1) {
                    return [400, {
                        "errors": [{
                            "message": "this field has error of type 1. Field: [itemId]. position: [0].",
                            "subject": "entries",
                            "type": "ValidationError"
                        }, {
                            "message": "this field has error of type 2. Field: [itemSuperType]. position: [0].",
                            "subject": "entries",
                            "type": "ValidationError"
                        }, {
                            "message": "this field has error of type 3. Field: [itemId]. position: [2].",
                            "subject": "entries",
                            "type": "ValidationError"
                        }]
                    }];
                }


                var node = lodash.find(nodes, {
                    uid: payload.uid
                });

                //START replacing entries
                entries = entries.filter(function(entry) {
                    return entry.navigationNodeUid !== node.uid;
                });
                payload.entries.forEach(function(entry) {
                    entry.navigationNodeUid = node.uid;
                });

                Array.prototype.push.apply(entries, payload.entries);
                //END replacing entries

                var destinationParentUid = payload.parentUid;
                var sourceParentUid = node.parentUid;
                var destinationPosition = payload.position;



                if (destinationParentUid !== sourceParentUid) {
                    lodash.forEach(nodes, function(_node) {
                        if (_node.parentUid === sourceParentUid && _node.position > node.position) {
                            _node.position--;
                        }
                        if (_node.parentUid === destinationParentUid && _node.position >= destinationPosition) {
                            _node.position++;
                        }
                    });
                } else {
                    var upperBoundry, lowerBoundry;
                    if (destinationPosition <= node.position) { //move up
                        upperBoundry = node.position;
                        lowerBoundry = destinationPosition;
                        lodash.forEach(nodes, function(_node) {
                            if (_node.parentUid === destinationParentUid && _node.position >= lowerBoundry && _node.position <= upperBoundry) {
                                _node.position++;
                            }
                        });
                    } else { //move down
                        upperBoundry = destinationPosition;
                        lowerBoundry = node.position;
                        lodash.forEach(nodes, function(_node) {
                            if (_node.parentUid === destinationParentUid && _node.position >= lowerBoundry && _node.position <= upperBoundry) {
                                _node.position--;
                            }
                        });
                    }

                }

                node.position = destinationPosition;
                node.parentUid = destinationParentUid;
                nodes = lodash.sortBy(nodes, "position");

                return [200, node];
            });

            $httpBackend.whenPOST(/sites\/.*\/catalogs\/.*\/versions\/.*\/navigations/).respond(function(method, url, data, headers) {
                var payload = JSON.parse(data);
                var uid = new Date().getTime();

                var nodeCountWithSameParent = nodes.filter(function(node) {
                    return node.parentUid === payload.parentUid;
                }).length;

                if (!payload.name) {
                    return [400, {
                        "errors": [{
                            "message": "name cannot be empty",
                            "subject": "name",
                            "type": "ValidationError"
                        }]
                    }];
                }

                var node = {
                    uid: uid,
                    name: payload.name,
                    title: payload.title,
                    parentUid: payload.parentUid,
                    hasChildren: false,
                    position: nodeCountWithSameParent,
                    entries: payload.entries || []
                };
                nodes.push(node);
                return [200, node];
            });

            $httpBackend.whenDELETE(/sites\/.*\/catalogs\/.*\/versions\/.*\/navigations/).respond(function(method, url, data, headers) {
                var uid = /cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/navigations\/(.*)/.exec(url)[1];

                nodes = nodes.filter(function(node) {
                    return node.uid !== uid;
                });

                return [200, {
                    navigationNodes: nodes.filter(function(node) {
                        return node.uid !== uid;
                    })
                }];
            });

            $httpBackend.whenGET(/cmswebservices\/.*\/navigationentrytypes/).respond(function(method, url, data, headers) {
                var entryTypes = {
                    "navigationEntryTypes": [{
                        "itemType": "AbstractCMSComponent"
                    }, {
                        "itemType": "AbstractPage"
                    }, {
                        "itemType": "Media"
                    }]
                };
                return [200, entryTypes];
            });

            function appendEntriesToNode(node) {
                var _entries = lodash.cloneDeep(entries);
                var entryArray = _entries.filter(function(entry) {
                    return entry.navigationNodeUid === node.uid;
                }).map(function(entry) {
                    delete entry.navigationNodeUid;
                    delete entry.uid;
                    return entry;
                }).sort(function(a, b) {
                    return a.position - b.position;
                });

                node.entries = entryArray;
                return node;
            }

        });
try {
    angular.module('smarteditloader').requires.push('navigationNodeMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('navigationNodeMocks');
} catch (e) {}

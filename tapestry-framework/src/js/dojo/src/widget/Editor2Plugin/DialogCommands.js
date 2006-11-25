
dojo.provide("dojo.widget.Editor2Plugin.DialogCommands");dojo.require("dojo.i18n.common");dojo.requireLocalization("dojo.widget", "common");dojo.requireLocalization("dojo.widget", "Editor2");dojo.require("dojo.widget.FloatingPane");dojo.widget.defineWidget(
"dojo.widget.Editor2Dialog",[dojo.widget.HtmlWidget, dojo.widget.FloatingPaneBase, dojo.widget.ModalDialogBase],{templatePath: dojo.uri.dojoUri("src/widget/templates/Editor2/EditorDialog.html"),modal: true,width: "",height: "",windowState: "minimized",displayCloseAction: true,contentFile: "",contentClass: "",fillInTemplate: function(args, frag){this.fillInFloatingPaneTemplate(args, frag);dojo.widget.Editor2Dialog.superclass.fillInTemplate.call(this, args, frag);},postCreate: function(){if(this.contentFile){dojo.require(this.contentFile);}
if(this.modal){dojo.widget.ModalDialogBase.prototype.postCreate.call(this);}else{with(this.domNode.style) {zIndex = 999;display = "none";}}
dojo.widget.FloatingPaneBase.prototype.postCreate.apply(this, arguments);dojo.widget.Editor2Dialog.superclass.postCreate.call(this);if(this.width && this.height){with(this.domNode.style){width = this.width;height = this.height;}}},createContent: function(){if(!this.contentWidget && this.contentClass){this.contentWidget = dojo.widget.createWidget(this.contentClass);this.addChild(this.contentWidget);}},show: function(){if(!this.contentWidget){dojo.widget.Editor2Dialog.superclass.show.apply(this, arguments);this.createContent();dojo.widget.Editor2Dialog.superclass.hide.call(this);}
if(!this.contentWidget || !this.contentWidget.loadContent()){return;}
this.showFloatingPane();dojo.widget.Editor2Dialog.superclass.show.apply(this, arguments);if(this.modal){this.showModalDialog();}
if(this.modal){this.bg.style.zIndex = this.domNode.style.zIndex-1;}},onShow: function(){dojo.widget.Editor2Dialog.superclass.onShow.call(this);this.onFloatingPaneShow();},closeWindow: function(){this.hide();dojo.widget.Editor2Dialog.superclass.closeWindow.apply(this, arguments);},hide: function(){if(this.modal){this.hideModalDialog();}
dojo.widget.Editor2Dialog.superclass.hide.call(this);},checkSize: function(){if(this.isShowing()){if(this.modal){this._sizeBackground();}
this.placeModalDialog();this.onResized();}}}
);dojo.widget.defineWidget(
"dojo.widget.Editor2DialogContent",dojo.widget.HtmlWidget,{widgetsInTemplate: true,postMixInProperties: function(){dojo.widget.HtmlWidget.superclass.postMixInProperties.apply(this, arguments);this.editorStrings = dojo.i18n.getLocalization("dojo.widget", "Editor2", this.lang);this.commonStrings = dojo.i18n.getLocalization("dojo.widget", "common", this.lang);},loadContent:function(){return true;},cancel: function(){this.parent.hide();}});dojo.lang.declare("dojo.widget.Editor2DialogCommand", dojo.widget.Editor2BrowserCommand,function(editor, name, dialogParas){this.dialogParas = dialogParas;},{execute: function(){if(!this.dialog){if(!this.dialogParas.contentFile || !this.dialogParas.contentClass){alert("contentFile and contentClass should be set for dojo.widget.Editor2DialogCommand.dialogParas!");return;}
this.dialog = dojo.widget.createWidget("Editor2Dialog", this.dialogParas);dojo.body().appendChild(this.dialog.domNode);dojo.event.connect(this, "destroy", this.dialog, "destroy");}
this.dialog.show();},getText: function(){return this.dialogParas.title || dojo.widget.Editor2DialogCommand.superclass.getText.call(this);}});
dojo.provide("tapestry.widget.TimePicker");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.html.style");
dojo.require("dojo.html.util");
dojo.require("dojo.html.metrics");
dojo.require("dojo.html.iframe");

dojo.widget.defineWidget(
        "tapestry.widget.TimePicker",
        dojo.widget.HtmlWidget,
{
    inputNodeId:null, // unique element id of form input text field
    optionValues:[], // param of 12 hour clock selection values
    selectedIndex:null, // param of what the default selected index should be
    dropdownClass:"dropdownCombobox",
    dropdownOptionClass:"dropdownOption",
    optionHoverClass:"optionHover",

    inputNode:null, // form input text node
    selectedNode:null, // currently selected node
    dropdownNode:null, // drop down div container
    bgIframe:null,
    options:[], // option div nodes
    dropdownPositioned:false,
    showing:false,

    postCreate: function() {
        this.inputNode = dojo.byId(this.inputNodeId);

        this.dropdownNode = document.createElement("div");
        this.dropdownNode.setAttribute("id", this.widgetId + "dropdown");
        this.dropdownNode.style["display"] = "none";
        dojo.html.setClass(this.dropdownNode, this.dropdownClass);

        var contDiv = document.createElement("div");
        this.dropdownNode.appendChild(contDiv);
        
        for (var i=0; i < this.optionValues.length; i++){
            var option = document.createElement("div");
            option.setAttribute("id", "combooption-" + i);
            dojo.html.setClass(option, this.dropdownOptionClass);
            option.appendChild(document.createTextNode(this.optionValues[i]));
            
            contDiv.appendChild(option);
            this.options.push(option);

            if (this.selectedIndex && i == this.selectedIndex){
                this.selectedNode = option;

                if (!this.inputNode.value || this.inputNode.value.length < 1){
                    this.inputNode.value=this.optionValues[i];
                }
            }

            dojo.event.connect(option, "onmouseover", this, "onOptionMouseOver");
            dojo.event.connect(option, "onmouseout", this, "onOptionMouseOut");
            dojo.event.connect(option, "onmousedown", this, "onOptionClicked");
        }

        var m = dojo.html.getCachedFontMeasurements();
        var st=this.dropdownNode.style;
        st["overflow"]="auto";
        st["zIndex"]=9000;
        st["position"]="absolute";
        st["width"]=(m["1em"] * 6) + "px"
        st["height"]=(m["1em"] * 11) + "px";

        dojo.body().appendChild(this.dropdownNode);

        if (dojo.render.html.ie){
            this.bgIframe = new dojo.html.BackgroundIframe();
            this.bgIframe.setZIndex(this.dropdownNode);
        }
        
        dojo.event.connect(this.inputNode, "onclick", this, "onInputClick");
        dojo.event.connect(this.inputNode, "onblur", this, "hide");
        
        dojo.event.connect(dojo.body(), "onkeyup", this, "onKeyUp");
    },

    onOptionMouseOver: function(evt) {
        if (!dojo.html.hasClass(evt.target, this.optionHoverClass)) {
            dojo.html.addClass(evt.target, this.optionHoverClass);
        }
    },

    onOptionMouseOut: function(evt) {
        dojo.html.removeClass(evt.target, this.optionHoverClass);
    },

    onChange:function() {},

    onOptionClicked: function(evt) {
        this.selectedNode=evt.target;
        
        this.inputNode.value=tapestry.html.getContentAsString(this.selectedNode);
        this.hide(evt);
        dojo.html.removeClass(this.selectedNode, this.optionHoverClass);

        this.onChange(evt);
    },

    onInputClick: function() {
        if (this.showing){
            this.hide();
            return;
        }
        
        this.show();

        if (this.selectedNode){
            dojo.html.scrollIntoView(this.selectedNode);
        }
    },

    onKeyUp: function(evt) {
        if (evt.keyCode == evt.KEY_ESCAPE) {
            this.hide(evt);
        }
    },

    hide: function(evt) {
        dojo.html.hide(this.dropdownNode);
        
        if (this.bgIframe){
            this.bgIframe.hide();
        }

        this.showing=false;
    },

    show: function(evt) {
        if (!this.dropdownPositioned){
            dojo.html.placeOnScreenAroundElement(this.dropdownNode, this.inputNode,
                                            null, dojo.html.boxSizing.BORDER_BOX,
                                            {'BL': 'TL', 'TL': 'BL'});
            this.dropdownPositioned = true;
        }

        dojo.html.show(this.dropdownNode);
        
        if (this.bgIframe){
            this.bgIframe.size(this.dropdownNode);
            this.bgIframe.show();
        }

        this.showing=true;
    },

    destroyRendering: function(finalize){
        try{
            dojo.widget.HtmlWidget.prototype.destroyRendering.call(this, finalize);

            dojo.event.disconnect(this.inputNode, "onclick", this, "onInputClick");
            dojo.event.disconnect(this.inputNode, "onblur", this, "hide");
            dojo.event.browser.clean(this.inputNode);
            
            dojo.dom.destroyNode(this.dropdownNode);
            delete this.dropdownNode;

            dojo.event.disconnect(dojo.body(), "onkeyup", this, "onKeyUp");

             if (this.bgIframe){
                this.bgIframe.remove();
            }
        } catch (e) { }
    },

    getValue:function(){
        return this.inputNode.value;
    }
}
);

dojo.provide("tapestry.form.validation");

dojo.require("dojo.validate.check");
dojo.require("dojo.html");
dojo.require("dojo.widget.*");

dojo.require("tapestry.widget.AlertDialog");

tapestry.form.validation={
	
	missingClass:"fieldMissing", // default css class that will be applied to fields missing a value
	invalidClass:"fieldInvalid", // default css class applied to fields with invalid data
	
	/**
	 * Main entry point for running form validation. The
	 * props object passed in contains a number of fields that 
	 * are managed by tapestry.form:
	 * 
	 * 		props = {
	 * 			validateForm:[true|false] // whether to run validation at all
	 * 			profiles:[profile1, profile2] // set of dojo.validate.check() style profiles
	 * 										  // that may have been registered with form
	 * 		}
	 * 
	 * The individual profiles will contain any of the data described by the dojo documentation
	 * for dojo.validate.check(). In addition to that, each profile will also have a corresponding
	 * string message to display if the specified condition has been met. For example, if you have
	 * specified that a select field named "select1" was required your profile would look something
	 * like:
	 * 
	 * 		profile = {
	 * 			"required":["select1"], // normal dojo.validate.check data
	 * 			"select1":{ // tapestry field/error type specific data
	 * 				"required":"You must select a value for select1."
	 * 			}
	 * 		}
	 * 
	 * It is intended for you to call dojo.validate.check(form, profile) for each profile
	 * stored in the "profiles" field, as well as deciding how to display errors / warnings.
	 * 
	 * @return Boolean indicating if form submission should continue. If false the form
	 * 			will ~not~ be submitted. 
	 */
	validateForm:function(form, props){
		if (!form) return false;
		if (!props) return true; // form exists but no profile? just submit I guess..
		if (!props.validateForm) return true;
		
		this.clearValidationDecorations(form, props);
		
		for (var i=0; i < props.profiles.length; i++) {
			var results=dojo.validate.check(form, props.profiles[i]);
			
			if (!this.processResults(form, results, props.profiles[i])) {
				this.summarizeErrors(form, results, props.profiles[i]);
				return false;
			}
		}
		
		return true;
	},
	
	/**
	 * Called for each registered profile on a form after 
	 * dojo.validate.check() has been called. This function is 
	 * expected to do UI related notifications of fields in error.
	 * 
	 * @param form The form that was validated.
	 * @param results The result of calling dojo.validate.check(form,profile)
	 * @param profile The original profile used to validate form, also holds 
	 * 				  validation error messages to be used for each field.
	 * 
	 * @return Boolean, if false form should not be submitted and all validation 
	 * 		   should be stopped. If true validation will continue and eventually
	 * 		   form will be submitted.
	 */
	processResults:function(form, results, profile){
		if (results.isSuccessful()) return true; 
		
		// TODO: Need to remove previous field validation
		// decorations for things fixed since last run
		var formValid=true;
		if (results.hasMissing()) {
			var missing=results.getMissing();
			for (var i=0; i < missing.length; i++) {
				this.handleMissingField(missing[i], profile);
			}
			
			formValid=false;
		}
		
		return formValid; // if got past successful everything is invalid
	},
	
	/**
	 * Default field decorator for missing fields.
	 * 
	 * @param field The field element that was missing data.
	 * @param profile The form validation profile.
	 */
	handleMissingField:function(field, profile){
		dojo.html.removeClass(field, this.invalidClass);
		
		if (!dojo.html.hasClass(field, this.missingClass)){
			dojo.html.prependClass(field, this.missingClass);
		}
	},
	
	/**
	 * Default field decorator for invalid fields.
	 * 
	 * @param field The field element that had invalid data.
	 * @param profile The form validation profile.
	 */
	handleInvalidField:function(field, profile){
		dojo.html.removeClass(field, this.missingClass);
		
		if (!dojo.html.hasClass(field, this.invalidClass)){
			dojo.html.prependClass(field, this.invalidClass);
		}
	},
	
	clearValidationDecorations:function(form, props){
		for (var i=0; i<form.elements.length; i++) {
			dojo.html.removeClass(form.elements[i], this.missingClass);
			dojo.html.removeClass(form.elements[i], this.invalidClass);
		}
	},
	
	/**
	 * Optionally allows an alert dialog/dhtml dialog/etc to 
	 * be displayed to user to alert them to the invalid state
	 * of their form if validation errors have occurred. 
	 * 
	 * @param form The form being validated.
	 * @param results Returned value of dojo.validate.check(form, profile)
	 * @param profile Validation profile definition 
	 */
	summarizeErrors:function(form, results, profile){
		var merrs=[];
		var ierrs=[];
		if (results.hasMissing()){
			var fields=results.getMissing();
			for (var i=0; i<fields.length; i++){
				if (profile[fields[i]] && profile[fields[i]]["required"]){
					merrs.push(profile[fields[i]]["required"]);
				}
			}
		}
		if (results.hasInvalid()){
			var fields=results.getInvalid();
			for (var i=0; i<fields.length; i++){
				if (profile[fields[i]] && profile[fields[i]]["required"]){
					ierrs.push(profile[fields[i]]["required"]);
				}
			}
		}
		
		var msg="";
		if (merrs.length > 0) {
			msg+='<ul class="missingList">';
			for (var i=0; i<merrs.length;i++) {
				msg+="<li>"+merrs[i]+"</li>";
			}
			msg+="</ul>";
		}
		if (ierrs.length > 0) {
			msg+='<ul class="invalidList">';
			for (var i=0; i<ierrs.length;i++) {
				msg+="<li>"+ierrs[i]+"</li>";
			}
			msg+="</ul>";
		}
		
		var ad=dojo.widget.byId("validationDialog");
		if (ad) {
			ad.setMessage(msg);
			ad.show();
			return;
		}
		
		var node=document.createElement("span");
		document.body.appendChild(node);
		var dialog=dojo.widget.createWidget("AlertDialog", 
						{
							widgetId:"validationDialog",
							message:msg
						}, node);
		dialog.show();
	}
}
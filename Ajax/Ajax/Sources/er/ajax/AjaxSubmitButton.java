package er.ajax;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicElementCreationException;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.ERXWOForm;
import er.extensions.ERXProperties;

/**
 * AjaxSubmitButton behaves just like a WOSubmitButton except that it submits in the background with an Ajax.Request.
 * 
 * @binding name the HTML name of this submit button (optional)
 * @binding value the HTML value of this submit button (optional)
 * @binding action the action to execute when this button is pressed
 * @binding id the HTML ID of this submit button
 * @binding class the HTML class of this submit button
 * @binding style the HTML style of this submit button
 * @binding onClick arbitrary Javascript to execute when the client clicks the button
 * @binding onClickBefore if the given function returns true, the onClick is executed.  This is to support confirm(..) dialogs. 
 * @binding onServerClick if the action defined in the action binding returns null, the value of this binding will be returned as javascript from the server
 * @binding onComplete JavaScript function to evaluate when the request has finished.
 * @binding onSuccess javascript to execute in response to the Ajax onSuccess event
 * @binding onFailure javascript to execute in response to the Ajax onFailure event
 * @binding onLoading javascript to execute when loading
 * @binding evalScripts evaluate scripts on the result
 * @binding button if false, it will display a link
 * @binding formName if button is false, you must specify the name of the form to submit
 * @binding functionName if set, the link becomes a javascript function instead
 * @binding updateContainerID the id of the AjaxUpdateContainer to update after performing this action
 * @binding showUI if functionName is set, the UI defaults to hidden; showUI re-enables it
 * @binding formSerializer the name of the javascript function to call to serialize the form
 * @binding elementName the element name to use (defaults to "a")
 * 
 * // PROTOTYPE EFFECTS
 * @binding effect synonym of afterEffect except it always applies to updateContainerID
 * @binding effectDuration synonym of afterEffectDuration except it always applies to updateContainerID
 * // PROTOTYPE EFFECTS
 * @binding beforeEffect the Scriptaculous effect to apply onSuccess ("highlight", "slideIn", "blindDown", etc);
 * @binding beforeEffectID the ID of the container to apply the "before" effect to (blank = try nearest container, then try updateContainerID)
 * @binding beforeEffectDuration the duration of the effect to apply before
 * // PROTOTYPE EFFECTS
 * @binding afterEffect the Scriptaculous effect to apply onSuccess ("highlight", "slideIn", "blindDown", etc);
 * @binding afterEffectID the ID of the container to apply the "after" effect to (blank = try nearest container, then try updateContainerID)
 * @binding afterEffectDuration the duration of the effect to apply after
 * 
 * // PROTOTYPE EFFECTS
 * @binding insertion JavaScript function to evaluate when the update takes place (or effect shortcuts like "Effect.blind", or "Effect.BlindUp")
 * @binding insertionDuration the duration of the before and after insertion animation (if using insertion) 
 * @binding beforeInsertionDuration the duration of the before insertion animation (if using insertion) 
 * @binding afterInsertionDuration the duration of the after insertion animation (if using insertion)
 *  
 * @property er.ajax.formSerializer the default form serializer to use for all ajax submits
 * 
 * @author anjo
 */
public class AjaxSubmitButton extends AjaxDynamicElement {
	// MS: If you change this value, make sure to change it in ERXAjaxApplication
  public static final String KEY_AJAX_SUBMIT_BUTTON_NAME = "AJAX_SUBMIT_BUTTON_NAME";
	// MS: If you change this value, make sure to change it in ERXAjaxApplication and in wonder.js
  public static final String KEY_PARTIAL_FORM_SENDER_ID = "_partialSenderID";

  public AjaxSubmitButton(String name, NSDictionary associations, WOElement children) {
    super(name, associations, children);
  }

  public static boolean isAjaxSubmit(WORequest request) {
	  return request.valueForKey(KEY_AJAX_SUBMIT_BUTTON_NAME) != null;
  }
  
  public boolean disabledInComponent(WOComponent component) {
    return booleanValueForBinding("disabled", false, component);
  }

  public String nameInContext(WOContext context, WOComponent component) {
    return (String) valueForBinding("name", context.elementID(), component);
  }

  public NSMutableDictionary createAjaxOptions(WOComponent component) {
	// PROTOTYPE OPTIONS
    NSMutableArray ajaxOptionsArray = new NSMutableArray();
    ajaxOptionsArray.addObject(new AjaxOption("onComplete", AjaxOption.SCRIPT));
    ajaxOptionsArray.addObject(new AjaxOption("onSuccess", AjaxOption.SCRIPT));
    ajaxOptionsArray.addObject(new AjaxOption("onFailure", AjaxOption.SCRIPT));
    ajaxOptionsArray.addObject(new AjaxOption("onLoading", AjaxOption.SCRIPT));
    ajaxOptionsArray.addObject(new AjaxOption("evalScripts", AjaxOption.BOOLEAN));
	ajaxOptionsArray.addObject(new AjaxOption("insertion", AjaxOption.SCRIPT));
	
    String name = nameInContext(component.context(), component);
    NSMutableDictionary options = AjaxOption.createAjaxOptionsDictionary(ajaxOptionsArray, component, associations());
    AjaxSubmitButton.fillInAjaxOptions(this, component, name, options);
    return options;
  }

  public static void fillInAjaxOptions(IAjaxElement element, WOComponent component, String submitButtonName, NSMutableDictionary options) {
    StringBuffer parametersBuffer = new StringBuffer();
    String systemDefaultFormSerializer = "Form.serializeWithoutSubmits";
    String defaultFormSerializer = ERXProperties.stringForKeyWithDefault("er.ajax.formSerializer", systemDefaultFormSerializer);
    String formSerializer = (String) element.valueForBinding("formSerializer", defaultFormSerializer, component);
    if (!defaultFormSerializer.equals(systemDefaultFormSerializer)) {
    	// _fs = formSerializer (but short)
	    options.setObjectForKey(formSerializer, "_fs");
    }
	// _asbn = AJAX_SUBMIT_BUTTON_NAME (but short)
    options.setObjectForKey("'" + submitButtonName + "'", "_asbn");

	// PROTOTYPE OPTIONS
    // default to true in javascript
    if ("true".equals(options.objectForKey("asynchronous"))) {
    	options.removeObjectForKey("asynchronous");
    }

	// PROTOTYPE OPTIONS
    // default to true in javascript
    if ("true".equals(options.objectForKey("evalScripts"))) {
    	options.removeObjectForKey("evalScripts");
    }
	AjaxUpdateContainer.expandInsertionFromOptions(options, element, component);
  }

  public void appendToResponse(WOResponse response, WOContext context) {
    WOComponent component = context.component();

    String functionName = (String)valueForBinding("functionName", null, component);
    String formName = (String)valueForBinding("formName", component);
    boolean showUI = (functionName == null || booleanValueForBinding("showUI", false, component));
    boolean showButton = showUI && booleanValueForBinding("button", true, component);
    String formReference;
    if ((!showButton || functionName != null) && formName == null) {
      formName = ERXWOForm.formName(context, null);
      if (formName == null) {
        throw new WODynamicElementCreationException("If button = false or functionName is not null, the containing form must have an explicit name.");
      }
    }
    if (formName == null) {
      formReference = "this.form";
    }
    else {
      formReference = "document." + formName;
    }
    
    StringBuffer onClickBuffer = new StringBuffer();

	String onClickBefore = (String)valueForBinding("onClickBefore", component);
	if (onClickBefore != null) {
		onClickBuffer.append("if (");
		onClickBuffer.append(onClickBefore);
		onClickBuffer.append(") {");
	}
	
	String updateContainerID = (String)valueForBinding("updateContainerID", component);
	
	// PROTOTYPE EFFECTS
	String beforeEffect = (String) valueForBinding("beforeEffect", component);
	if (beforeEffect != null) {
		onClickBuffer.append("new ");
		onClickBuffer.append(AjaxUpdateLink.fullEffectName(beforeEffect));
		onClickBuffer.append("('");
		
		String beforeEffectID = (String) valueForBinding("beforeEffectID", component);
		if (beforeEffectID == null) {
			beforeEffectID = AjaxUpdateContainer.currentUpdateContainerID();
			if (beforeEffectID == null) {
				beforeEffectID = updateContainerID;
			}
		}
		onClickBuffer.append(beforeEffectID);
		
		onClickBuffer.append("', { ");
		
		String beforeEffectDuration = (String) valueForBinding("beforeEffectDuration", component);
		if (beforeEffectDuration != null) {
			onClickBuffer.append("duration: ");
			onClickBuffer.append(beforeEffectDuration);
			onClickBuffer.append(", ");
		}
		
		onClickBuffer.append("queue:'end', afterFinish: function() {");
	}

	if (updateContainerID != null) {
		onClickBuffer.append("ASB.update('" + updateContainerID + "',");
	}
	else {
		onClickBuffer.append("ASB.request(");
	}
	onClickBuffer.append(formReference);
	if (valueForBinding("functionName", component) != null) {
		onClickBuffer.append(",additionalParams");
	}
	else {
		onClickBuffer.append(",null");
	}
	onClickBuffer.append(",");
	
    NSMutableDictionary options = createAjaxOptions(component);
	
	AjaxUpdateLink.addEffect(options, (String) valueForBinding("effect", component), updateContainerID, (String) valueForBinding("effectDuration", component));
	String afterEffectID = (String) valueForBinding("afterEffectID", component);
	if (afterEffectID == null) {
		afterEffectID = AjaxUpdateContainer.currentUpdateContainerID();
		if (afterEffectID == null) {
			afterEffectID = updateContainerID;
		}
	}
	AjaxUpdateLink.addEffect(options, (String) valueForBinding("afterEffect", component), afterEffectID, (String) valueForBinding("afterEffectDuration", component));
	
    AjaxOptions.appendToBuffer(options, onClickBuffer, context);
    onClickBuffer.append(")");
    String onClick = (String) valueForBinding("onClick", component);
    if (onClick != null) {
      onClickBuffer.append(";");
      onClickBuffer.append(onClick);
    }
	
	if (beforeEffect != null) {
		onClickBuffer.append("}});");
	}

    if (onClickBefore != null) {
    	onClickBuffer.append("}");
    }

    
    if (functionName != null) {
      	AjaxUtils.appendScriptHeader(response);
    	response.appendContentString(functionName + " = function(additionalParams) { " + onClickBuffer + " }\n");
    	AjaxUtils.appendScriptFooter(response);
    }
    if (showUI) {
    	boolean disabled = disabledInComponent(component);
    	String elementName = (String) valueForBinding("elementName", "a", component);
    	boolean useButtonTag = ERXProperties.booleanForKeyWithDefault("er.extensions.ERXPatcher.DynamicElementsPatches.SubmitButton.useButtonTag", false);
    	
	    if (showButton) {
	      elementName = useButtonTag ? "button" : "input";
	      response.appendContentString("<" + elementName + " ");
	      appendTagAttributeToResponse(response, "type", "button");
	      String name = nameInContext(context, component);
	      appendTagAttributeToResponse(response, "name", name);
	      appendTagAttributeToResponse(response, "value", valueForBinding("value", component));
	      
	      if (disabled) {
	    	  appendTagAttributeToResponse(response, "disabled", "disabled");
	      }
	    }
	    else {
			boolean isATag = "a".equalsIgnoreCase(elementName);
			if (isATag) {
				response.appendContentString("<a href = \"javascript:void(0)\" ");
			}
			else {
				response.appendContentString("<" + elementName + " ");
			}
	    }
	    appendTagAttributeToResponse(response, "class", valueForBinding("class", component));
	    appendTagAttributeToResponse(response, "style", valueForBinding("style", component));
	    appendTagAttributeToResponse(response, "id", valueForBinding("id", component));
    	if (functionName == null) {
    		appendTagAttributeToResponse(response, "onclick", onClickBuffer.toString());
    	}
    	else {
    		appendTagAttributeToResponse(response, "onclick", functionName + "()");
    	}
	    if (showButton && !useButtonTag) {
	      response.appendContentString(" />");
	    }
	    else {
	      response.appendContentString(">");
	      if (hasChildrenElements()) {
	        appendChildrenToResponse(response, context);
	      }
	      response.appendContentString("</" + elementName + ">");
	    }
    }
    super.appendToResponse(response, context);
  }

  protected void addRequiredWebResources(WOResponse res, WOContext context) {
    addScriptResourceInHead(context, res, "prototype.js");
	addScriptResourceInHead(context, res, "effects.js");
    addScriptResourceInHead(context, res, "wonder.js");
  }

  public WOActionResults invokeAction(WORequest worequest, WOContext wocontext) {
    WOActionResults result = null;
    WOComponent wocomponent = wocontext.component();

    String nameInContext = nameInContext(wocontext, wocomponent);
    boolean shouldHandleRequest = (!disabledInComponent(wocomponent) && wocontext._wasFormSubmitted()) && ((wocontext._isMultipleSubmitForm() && nameInContext.equals(worequest.formValueForKey(KEY_AJAX_SUBMIT_BUTTON_NAME))) || !wocontext._isMultipleSubmitForm());
    if (shouldHandleRequest) {
      AjaxUpdateContainer.setUpdateContainerID(worequest, (String) valueForBinding("updateContainerID", wocomponent));
      wocontext._setActionInvoked(true);
      result = handleRequest(worequest, wocontext);
      AjaxUtils.updateMutableUserInfoWithAjaxInfo(wocontext);
    }
    
    return result;
  }

  public WOActionResults handleRequest(WORequest worequest, WOContext wocontext) {
    WOResponse response = AjaxUtils.createResponse(worequest, wocontext);
    WOComponent wocomponent = wocontext.component();
    Object obj = valueForBinding("action", wocomponent);
    String onClickServer = (String) valueForBinding("onClickServer", wocomponent);
    if (onClickServer != null) {
		AjaxUtils.appendScriptHeaderIfNecessary(worequest, response);
		response.appendContentString(onClickServer);
		AjaxUtils.appendScriptFooterIfNecessary(worequest, response);
    }
    return response;
  }

}
/**
 * Copyright (c) 2014, Clemens Rabe
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

ChangeAlternativePasswordWindow = Ext.extend(Ext.Window,{

    titleText: 'Change Alternative Password',
    passwordText: 'Password',
    okText: 'Ok',
    cancelText: 'Cancel',
    connectingText: 'Connecting',
    failedText: 'change password failed!',
    waitMsgText: 'Sending data...',
    
    submitText: 'Submit ...',
    loadingText: 'Loading ...',
    
    errorTitleText: 'Error',
    errorMsgText: 'Could not load config.',
    errorSubmitMsgText: 'Could not submit config.',

    configUrl: restUrl + 'plugins/secondpass.json',
    loadMethod: 'GET',
    submitMethod: 'POST',
    
    initComponent: function(){
	
	var config = {
	    layout:'fit',
	    width:300,
	    height:170,
	    closable: false,
	    resizable: false,
	    plain: true,
	    border: false,
	    modal: true,
	    title: this.titleText,
	    
	    items: [{
		id: 'changePasswordForm',
		xtype : 'form',
		url: restUrl + 'plugins/secondpass.json',
		frame: true,
		monitorValid: true,
		defaultType: 'textfield',

		listeners: {
		    render: function(){
			if ( this.onLoad && Ext.isFunction( this.onLoad ) ){
			    this.onLoad(this.el);
			}
		    },
		    scope: this
		},

		items : [{
		    xtype: 'textfield',
		    name: 'username',
		    fieldLabel: "Username",
		    helpText: "Username - do not change",
		    allowBlank : false,
		    hidden : true
		},{
		    xtype: 'textfield',
		    name: 'secondPass',
		    fieldLabel: "Alternative Password",
		    helpText: "Alternative password",
		    allowBlank : false
		}],

		buttons: [{
		    text: "Save",
		    scope: this,
		    formBind: true,
		    handler: this.submitForm
		},{
		    text: "Generate",
		    scope: this,
		    handler: this.generateRandomPassword
		},{
		    text: "Cancel",
		    scope: this,
		    handler: this.cancel
		}]

	    }]
	};
	
	Ext.apply(this, Ext.apply(this.initialConfig, config));
	ChangeAlternativePasswordWindow.superclass.initComponent.apply(this, arguments);
    },
    

    load: function(values){
	Ext.getCmp('changePasswordForm').getForm().loadRecord({
	    success: true,
	    data: values
	});
    },

    submitForm: function(){
	var form = Ext.getCmp('changePasswordForm').getForm();
	if ( this.onSubmit && Ext.isFunction( this.onSubmit ) ){
	    this.onSubmit( form.getValues() );
	}
	this.close();
    },

    cancel: function(){
	this.close();
    },

    generateRandomPassword: function(){
	var form  = Ext.getCmp('changePasswordForm').getForm();
	var field = form.findField( 'secondPass' );
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var minLength = 30;
	var maxLength = 60;
	var length    = Math.floor(Math.random()*(maxLength-minLength+1)+minLength);
	var password  = '';

	for (var i=0; i<length; i++) {
            var rnum = Math.floor(Math.random() * chars.length);
            password += chars.substring(rnum,rnum+1);
        }

	field.setValue( password );
    },

    onSubmit: function(values){
	this.el.mask(this.submitText);
	Ext.Ajax.request({
	    url: this.configUrl,
	    method: this.submitMethod,
	    jsonData: values,
	    scope: this,
	    disableCaching: true,
	    success: function(response){
		this.el.unmask();
	    },
	    failure: function(result){
		this.el.unmask();
		main.handleRestFailure(
		    result, 
		    null, 
		    this.failedText
		);
	    }
	});
    },
    
    onLoad: function(el){
	var tid = setTimeout( function(){ el.mask(this.loadingText); }, 100);
	Ext.Ajax.request({
	    url: this.configUrl,
	    method: this.loadMethod,
	    scope: this,
	    disableCaching: true,
	    success: function(response){
		var obj = Ext.decode(response.responseText);
		this.load(obj);
		clearTimeout(tid);
		el.unmask();
	    },
	    failure: function(result){
		el.unmask();
		clearTimeout(tid);
		main.handleRestFailure(
		    result, 
		    null, 
		    this.failedText
		);
	    }
	});
    }
    
});


/* Add section to the navigationPanel */
loginCallbacks.push(function(){
  var navPanel = Ext.getCmp('navigationPanel');
  var count = navPanel.count() - 1;
  navPanel.insertSection(count, {
    title: 'Alternative Password',
    items: [{
      label: 'Modify Password',
      fn: function(){
          new ChangeAlternativePasswordWindow().show();
      }
    }]
  });
});
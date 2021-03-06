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
package sonia.scm.plugins.secondpass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Singleton;


/**
 * Configuration container of the SecondPass plugin.
 * 
 * @author Clemens Rabe
 *
 */
@Singleton
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecondPassPluginConfig {
	
	public SecondPassPluginConfig()
	{
		
	}
	
	public Boolean usePAM()
	{
		return enablePAM_b;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public void setUsePAM(Boolean usePam)
	{
		enablePAM_b = usePam;
	}
	
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	//~--- fields ---------------------------------------------------------------

	@XmlElement(name = "enable-pam")
	private Boolean enablePAM_b = true;
	
	@XmlElement(name = "service-name")
	private String serviceName = "sshd";
}

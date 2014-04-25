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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.SCMContextProvider;
import sonia.scm.plugin.ext.Extension;
import sonia.scm.store.Store;
import sonia.scm.store.StoreFactory;
import sonia.scm.user.User;
import sonia.scm.user.UserManager;
import sonia.scm.web.security.AuthenticationHandler;
import sonia.scm.web.security.AuthenticationResult;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The authentication handler to support the secondary password.
 * 
 * @author clemens
 * 
 */
@Singleton
@Extension
public class SecondPassAuthenticationHandler implements AuthenticationHandler {
	/** The authentication type. */
	public static final String TYPE = "secondPass";

	/** The type used for the store. */
	public static final String STORETYPE = "secondPass";

	/** the logger for AutoLoginAuthenticationHandler */
	private static final Logger logger = LoggerFactory
			.getLogger(SecondPassAuthenticationHandler.class);

	/** The configuration of the plugin. */
	private SecondPassConfig config;

	/** The store of the configuration. */
	private Store<SecondPassConfig> store;

	/** The user manager. */
	private UserManager userManager;

	/**
	 * Constructor.
	 * 
	 * @param userManager
	 *            - The user manager.
	 * @param storeFactory
	 *            - The factory to get the store.
	 */
	@Inject
	public SecondPassAuthenticationHandler(UserManager userManager,
			StoreFactory storeFactory) {
		this.userManager = userManager;
		store = storeFactory.getStore(SecondPassConfig.class, STORETYPE);
	}

	/**
	 * Initialize the AutoLoginAuthenticationHandler.
	 */
	@Override
	public void init(SCMContextProvider context) {
		config = store.get();

		if (config == null) {
			config = new SecondPassConfig();
			store.set(config);
		}
	}

	/**
	 * Close the AutoLoginAuthenticationHandler.
	 */
	@Override
	public void close() throws IOException {
	}

	/**
	 * Get the type of the AutoLoginAuthenticationHandler.
	 */
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public AuthenticationResult authenticate(HttpServletRequest request,
			HttpServletResponse response, String username, String password) {
		AuthenticationResult result = AuthenticationResult.NOT_FOUND;

		// Search for the user in the user manager
		User user = userManager.get(username);

		if (user == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("user {} not available from the user manager",
						username);
			}
		} else {
			// Search for the user in the configuration
			boolean userFound = false;

			for (SecondPassConfigEntry entry : config.getUsers()) {
				if (entry.getUsername().equals(username)) {
					userFound = true;

					if (logger.isDebugEnabled()) {
						logger.debug("entry for user {} found", username);
					}

					if (entry.getSecondPass().equals(password)) {
						result = new AuthenticationResult(user);
					}

					if (logger.isDebugEnabled()) {
						if (result != null) {
							logger.debug(
									"user {} authenticated using alternative password",
									username);
						} else {
							logger.debug(
									"alternative password for user {} does not match",
									username);
						}
					}

					break;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"secondPass entry for user {} found, that does not match current user",
								entry.getUsername());
					}
				}
			}

			if (!userFound) {
				if (logger.isDebugEnabled()) {
					logger.debug("no entry for user {} found", username);
				}
			}
		}

		return result;
	}

	public SecondPassConfigEntry getCurrentUserEntry() {
		Subject subject = SecurityUtils.getSubject();
		User currentUser = subject.getPrincipals().oneByType(User.class);

		for (SecondPassConfigEntry entry : config.getUsers()) {
			if (entry.getUsername().equals(currentUser.getName())) {
				if (logger.isDebugEnabled()) {
					logger.debug("found secondpass entry for user {}",
							currentUser.getName());
				}

				return entry;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("create temporary secondpass entry for user {}",
					currentUser.getName());
		}
		SecondPassConfigEntry entry = new SecondPassConfigEntry();
		entry.setUsername( currentUser.getName() );
		entry.setSecondPass( SecondPassHelper.generateRandomPassword(30) );
		return entry;
	}

	public void setCurrentUserEntry(SecondPassConfigEntry entry) {
		Subject subject = SecurityUtils.getSubject();
		User currentUser = subject.getPrincipals().oneByType(User.class);

		// The entry must have the same username
		if (! entry.getUsername().equals(currentUser.getName())) {
			logger.error(
					"Username in entry is {}, but current user is {}. Abort update.",
					entry.getUsername(), currentUser.getName());
			return;
		}

		for (SecondPassConfigEntry configEntry : config.getUsers()) {
			if (configEntry.getUsername().equals(entry.getUsername())) {
				if (logger.isDebugEnabled()) {
					logger.debug("found secondpass entry for user {}",
							entry.getUsername());
				}

				configEntry.setSecondPass(entry.getSecondPass());
				store.set(config);

				if (logger.isDebugEnabled()) {
					logger.debug("updated second password for user {}",
							entry.getUsername());
				}

				return;
			}
		}

		// Still here, then there is no existing record. Add it to the list...
		config.getUsers().add(entry);
		store.set(config);

		if (logger.isDebugEnabled()) {
			logger.debug("added new entry for user {}", entry.getUsername());
		}
	}
}

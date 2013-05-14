// Copyright (C) 2013 Steffen Gebert, for TYPO3 Association
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.typo3.gerrit.plugins.avatars.typo3org;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.reviewdb.client.AccountExternalId;
import com.google.gerrit.reviewdb.client.AccountExternalId.Key;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.avatar.AvatarProvider;
import com.google.gerrit.server.config.CanonicalWebUrl;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nullable;

@Listen
@Singleton
public class ExternalUrlAvatarProvider implements AvatarProvider {

  private static final String REPLACE_MARKER = "%s";

  private final boolean ssl;
  private String externalAvatarUrl;
  private String avatarChangeUrl;

  @Inject
  ExternalUrlAvatarProvider(@CanonicalWebUrl @Nullable String canonicalUrl,
		  @GerritServerConfig Config cfg) {
    externalAvatarUrl = cfg.getString("avatar", null, "url");
    avatarChangeUrl = cfg.getString("avatar", null, "changeUrl");
    ssl = canonicalUrl != null && canonicalUrl.startsWith("https://");
  }

  @Override
  public String getUrl(IdentifiedUser forUser, int imageSize) {

    if (externalAvatarUrl == null) {
      Logger log = LoggerFactory.getLogger(ExternalUrlAvatarProvider.class);
      log.warn("Avatar URL is not configured, cannot show avatars. Please configure avatar.url in etc/gerrit.config");
      return null;
    }

    if (!externalAvatarUrl.contains(REPLACE_MARKER)) {
        Logger log = LoggerFactory.getLogger(ExternalUrlAvatarProvider.class);
        log.warn("Avatar provider url '" + externalAvatarUrl + "' does not contain " + REPLACE_MARKER + ", so cannot replace it with username");
        return null;
    }

    if (ssl && externalAvatarUrl.startsWith("http://")) {
    	externalAvatarUrl = externalAvatarUrl.replace("http://", "https://");
    }

    return replaceInUrl(externalAvatarUrl, forUser.getUserName());
  }

  @Override
  public String getChangeAvatarUrl(IdentifiedUser forUser) {

    return replaceInUrl(avatarChangeUrl, forUser.getUserName());
  }


  private String replaceInUrl(String url, String replacement) {

    if (replacement == null || url == null || url.contains(REPLACE_MARKER) == false) {
      return url;
    }

    String encodedReplacement = null;
    try {
      encodedReplacement = URLEncoder.encode(replacement, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      Logger log = LoggerFactory.getLogger(ExternalUrlAvatarProvider.class);
      log.warn("Weird thing, UTF-8 as encoding is not supported");
      return null;
    }

    return url.replace(REPLACE_MARKER, encodedReplacement);
  }
}

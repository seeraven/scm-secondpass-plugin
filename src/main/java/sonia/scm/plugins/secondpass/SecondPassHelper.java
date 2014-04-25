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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Helper for the SecondPass plugin.
 * 
 * @author Clemens Rabe
 */
public class SecondPassHelper
{

  /**
   * Generate a random password string.
   * 
   * @param length
   *          - The length of the password.
   * @return The random password string.
   */
  public static String generateRandomPassword(int length)
  {
    Random random = new Random();
    String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    char[] text = new char[length];
    for (int i = 0; i < length; i++)
    {
      text[i] = charSet.charAt(random.nextInt(charSet.length()));
    }

    return new String(text);
  }

  
  /**
   * Split the given comma separated string and return a set of strings.
   * 
   * @param groups
   *          - The comma separated string
   * @return The set of strings.
   */
  public static Set<String> splitGroups(String groups)
  {
    Set<String> groupList = new HashSet<String>();

    for (String element : groups.split(","))
    {
      String trimmed = element.trim();

      if (!trimmed.isEmpty())
        groupList.add(trimmed);
    }

    return groupList;
  }
}

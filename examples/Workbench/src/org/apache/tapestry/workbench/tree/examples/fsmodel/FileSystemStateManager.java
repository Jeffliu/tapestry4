/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation", "Tapestry" 
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" 
 *    or "Tapestry", nor may "Apache" or "Tapestry" appear in their 
 *    name, without prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE TAPESTRY CONTRIBUTOR COMMUNITY
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.tapestry.workbench.tree.examples.fsmodel;

import java.io.File;

import org.apache.tapestry.contrib.tree.model.ITreeDataModel;
import org.apache.tapestry.contrib.tree.model.ITreeModel;
import org.apache.tapestry.contrib.tree.model.ITreeNode;
import org.apache.tapestry.contrib.tree.model.ITreeSessionStateManager;
import org.apache.tapestry.contrib.tree.model.ITreeStateModel;
import org.apache.tapestry.contrib.tree.simple.SimpleTreeModel;

/**
 * @author ceco
 */
public class FileSystemStateManager implements ITreeSessionStateManager {
    private String m_strRootDir;

    /**
     * Constructor for FileSystemStateManager.
     */
    public FileSystemStateManager(String strRootDir) {
        super();
        m_strRootDir = strRootDir;
    }

    /**
     * @see org.apache.tapestry.contrib.tree.model.ITreeSessionStateManager#getSessionState(ITreeModel)
     */
    public Object getSessionState(ITreeModel objModel) {
        return objModel.getTreeStateModel();
    }

    /**
     * @see org.apache.tapestry.contrib.tree.model.ITreeSessionStateManager#getModel(Object)
     */
    public ITreeModel getModel(Object objSessionState) {
        ITreeStateModel objStateModel = (ITreeStateModel) objSessionState;

        ITreeNode objParent;

        if (m_strRootDir == null || "".equals(m_strRootDir)) {
            objParent = new FileSystem();
        } else {
            FolderObject objFolder = new FolderObject(null, new File(m_strRootDir), true);
            objFolder.reload();
            objParent = objFolder;
        }

        ITreeDataModel objDataModel = new FileSystemDataModel(objParent);
        ITreeModel objModel = new SimpleTreeModel(objDataModel,
                                                    objStateModel);

        return objModel;
    }
}

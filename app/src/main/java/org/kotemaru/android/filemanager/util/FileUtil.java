// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.filemanager.util;

import org.kotemaru.android.filemanager.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static void copy(Node srcNode, Node dstNode) throws IOException {
        InputStream src = srcNode.getInputStream();
        try {
            OutputStream dst = dstNode.getOutputStream();
            try {
                byte[] buff = new byte[4096];
                int n;
                while ((n=src.read(buff))>0) {
                    dst.write(buff,0,n);
                }
            } finally {
                dst.close();
            }
        } finally {
            src.close();
        }
    }

}

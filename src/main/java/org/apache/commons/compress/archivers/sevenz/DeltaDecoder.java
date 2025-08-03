/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.commons.compress.archivers.sevenz;

import org.checkerframework.dataflow.qual.SideEffectFree;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.Impure;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.tukaani.xz.DeltaOptions;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.UnsupportedOptionsException;

class DeltaDecoder extends CoderBase {
    @SideEffectFree
    @Impure
    DeltaDecoder() {
        super(Number.class);
    }

    @Impure
    @Override
    InputStream decode(final InputStream in, final Coder coder, byte[] password) throws IOException {
        return new DeltaOptions(getOptionsFromCoder(coder)).getInputStream(in);
    }

    @Impure
    @Override
    OutputStream encode(final OutputStream out, final Object options) throws IOException {
        int distance = numberOptionOrDefault(options, 1);
        try {
            return new DeltaOptions(distance).getOutputStream(new FinishableWrapperOutputStream(out));
        } catch (UnsupportedOptionsException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Pure
    @Impure
    @Override
    byte[] getOptionsAsProperties(Object options) {
        return new byte[] {
            (byte) (numberOptionOrDefault(options, 1) - 1)
        };
    }

    @SideEffectFree
    @Impure
    @Override
    Object getOptionsFromCoder(Coder coder, InputStream in) {
        return Integer.valueOf(getOptionsFromCoder(coder));
    }

    @Pure
    private int getOptionsFromCoder(Coder coder) {
        if (coder.properties == null || coder.properties.length == 0) {
            return 1;
        }
        return (0xff & coder.properties[0]) + 1;
    }
}

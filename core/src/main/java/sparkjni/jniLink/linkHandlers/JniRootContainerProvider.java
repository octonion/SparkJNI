/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sparkjni.jniLink.linkHandlers;

import sparkjni.jniLink.linkContainers.ImmutableJniRootContainer;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.utils.JniUtils;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Value.Immutable
public abstract class JniRootContainerProvider {
    public JniRootContainer buildJniRootContainer(@Nonnull String path, @Nonnull String appName) {
        File nativeDir = new File(path);
        JniUtils.checkNativePath(nativeDir);
        List<JniHeader> jniHeaders = new CopyOnWriteArrayList<>();
        for (File file : new File(path).listFiles())
            processFile(file, jniHeaders);

        return ImmutableJniRootContainer.builder()
                .path(path)
                .appName(appName)
                .jniHeaders(jniHeaders)
                .build();
    }

    private void processFile(@Nonnull File file, @Nonnull List<JniHeader> jniHeaders) {
        try {
            if(JniUtils.isJniNativeFunction(file.toPath()))
                jniHeaders.add(
                        ImmutableJniHeaderProvider.builder()
                                .jniHeaderFile(file)
                                .build()
                                .buildJniHeader()
                );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

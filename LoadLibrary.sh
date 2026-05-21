curl -L -o minecraft-assets.zip https://github.com/InventivetalentDev/minecraft-assets/zipball/refs/heads/1.21.11
unzip -q minecraft-assets.zip
dir=$(find . -maxdepth 1 -type d -name "InventivetalentDev-*" | head -n 1)
mv "$dir" "1.21.11"
rm -rf minecraft-assets.zip
curl --create-dirs -o lib/jar/lwjgl-glfw.jar https://build.lwjgl.org/stable/bin/lwjgl-glfw/lwjgl-glfw.jar
curl --create-dirs -o lib/jar/lwjgl-opengl.jar https://build.lwjgl.org/stable/bin/lwjgl-opengl/lwjgl-opengl.jar
curl --create-dirs -o lib/jar/lwjgl-stb.jar https://build.lwjgl.org/stable/bin/lwjgl-stb/lwjgl-stb.jar
curl --create-dirs -o lib/jar/lwjgl.jar https://build.lwjgl.org/stable/bin/lwjgl/lwjgl.jar
curl --create-dirs -o lib/natives-linux/libglfw.so https://build.lwjgl.org/stable/linux/arm64/libglfw.so
curl --create-dirs -o lib/natives-linux/liblwjgl_opengl.so https://build.lwjgl.org/stable/linux/arm64/liblwjgl_opengl.so
curl --create-dirs -o lib/natives-linux/liblwjgl_stb.so https://build.lwjgl.org/stable/linux/arm64/liblwjgl_stb.so
curl --create-dirs -o lib/natives-linux/liblwjgl.so https://build.lwjgl.org/stable/linux/arm64/liblwjgl.so
curl --create-dirs -o lib/natives-macos/libglfw.dylib https://build.lwjgl.org/stable/macosx/arm64/libglfw.dylib
curl --create-dirs -o lib/natives-macos/liblwjgl_opengl.dylib https://build.lwjgl.org/stable/macosx/arm64/liblwjgl_opengl.dylib
curl --create-dirs -o lib/natives-macos/liblwjgl_stb.dylib https://build.lwjgl.org/stable/macosx/arm64/liblwjgl_stb.dylib
curl --create-dirs -o lib/natives-macos/liblwjgl.dylib https://build.lwjgl.org/stable/macosx/arm64/liblwjgl.dylib
curl --create-dirs -o lib/jar/org.jar https://maven.jans.io/maven/org/json/json/20231013/json-20231013.jar
attribute vec4 vertexCol;
attribute vec4 vertexPos;

void main(void) {
	gl_Position=gl_ModelViewProjectionMatrix*vertexPos;
    gl_FrontColor = vertexCol;  
}



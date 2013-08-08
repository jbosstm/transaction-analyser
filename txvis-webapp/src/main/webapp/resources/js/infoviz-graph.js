/*
 * The below code has is taken from the publicly available example at:
 * http://philogb.github.io/jit/static/v20/Jit/Examples/Spacetree/example1.js
 * with very minor modification.
 */

var labelType, useGradients, nativeTextSupport, animate;

(function() {
    var ua = navigator.userAgent,
        iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
        typeOfCanvas = typeof HTMLCanvasElement,
        nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
        textSupport = nativeCanvasSupport
            && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
    //I'm setting this based on the fact that ExCanvas provides text support for IE
    //and that as of today iPhone/iPad current text support is lame
    labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
    nativeTextSupport = labelType == 'Native';
    useGradients = nativeCanvasSupport;
    animate = !(iStuff || !nativeCanvasSupport);
})();

function init(){
    //init data
    //end
    //init Spacetree
    //Create a new ST instance
    var st = new $jit.ST({
        //id of viz container element
        injectInto: 'infovis',
        //set duration for the animation
        duration: 800,
        transition: $jit.Trans.Quart.easeInOut,
        levelsToShow: 30,
        offsetX: 350,
        constrained: false,
        levelDistance: 50,
        //enable panning
        Navigation: {
            enable:false,
            panning:false
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            type: 'rectangle',
            color: '#aaa',
            height: 40,
            width: 150,
            overridable: true
        },

        Edge: {
            type: 'bezier',
            overridable: true
        },

        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function(label, node){
            label.id = node.id;
            label.innerHTML = node.name;
            //set label styles
            var style = label.style;

            style.cursor = 'pointer';
            style.color = '#333';
            style.fontSize = '0.8em';
            style.textAlign= 'center';
            style.width = 150 + 'px';
            style.height = 20 + 'px';
            style.paddingTop = '10px';
            style.zIndex = '1000';
        },

        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function(node){

            if (node.data.isResource == 'true') {
               node.data.$type = 'ellipse';
               node.data.$color = '#d9edf7';
            }

            if (node.data.vote == 'COMMIT') {
                node.data.$color = '#468847';
            }

            if (node.data.xaException) {
                node.data.$color = '#b94a48';
            }
        },

        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#eed";
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    //load json data
    st.loadJSON(json);
    //compute node positions and layout
    st.compute();
    //optional: make a translation of the tree
    //st.geom.translate(new $jit.Complex(-200, 0), "current");
    //emulate a click on the root node.
    st.onClick(st.root);
}
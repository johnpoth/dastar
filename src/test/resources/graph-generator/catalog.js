var meny = Meny.create({
    menuElement: document.querySelector('.meny'),
    contentsElement: document.querySelector('.contents')
});

// supports for IE7+, Firefox, Chrome, Opera, Safari

  xmlDoc= loadXMLDoc("graph.xml");
    if(xmlDoc == null ){
        alert("Could not get catalog");
    }
display(xmlDoc);


Opentip.styles.tooltip = {
    extends: "standard",
    target: true,
    delay: 0,
    borderRadius: 10,
    borderWidth: 5,
    stemLength: 18,
    stemBase: 20,
    shadowBlur: 10,
    shadowOffset: [8, 8],
    borderColor: "#333",
    background: "#FFFFFF",
    tipJoint: "bottom left",
    targetJoint: "top right",
    stem: "bottom left",
    padding: "0px"
};

function loadXMLDoc(filename)
{

    if (window.XMLHttpRequest)
    {
        xhttp=new XMLHttpRequest();
    }
    else // code for IE5 and IE6
    {
        xhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.open("GET",filename,false);
    xhttp.send();
    return xhttp.responseXML;
}

function display(catalog) {
    var graph = new dagreD3.Digraph();

    var nodes = catalog.getElementsByTagName("nodes");



    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var label = getNodeValue(node, "label");
        var moves = getNodeValue(node, "moves");
        graph.addNode(label, {  moves: moves });
    }

    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var edges = node.getElementsByTagName("edge")

        for (var j = 0; j < edges.length; j++) {
            var edge=edges[j];
            var edgeLabel = getNodeValue(edge,"label");
            var startNode = getNodeValue(edge,"startNode");
            var endNode = getNodeValue(edge,"endNode");
            graph.addEdge(null, startNode, endNode, { label: edgeLabel });
        }
    }
    var renderer = new dagreD3.Renderer();

    var oldDrawNodes = renderer.drawNodes();
    renderer.drawNodes(function(graph, root) {
        var svgNodes = oldDrawNodes(graph, root);
        svgNodes.each(function(u) {
            d3.select(this)
                .on("mouseover", function() {
                    d3.select(this).classed("active", true);
                })
                .on("mouseout", function() {
                    d3.select(this).classed("active", false);
                });
            if (graph.node(u).moves) {
                var content = "<table class='tooltip'>";
                content += "<tr><th colspan='2'>" + toUpperCase("MOVES: " + graph.node(u).moves) + "</th></tr>";
                var tooltip = new Opentip(this, content, { style: "standard" });
            }
        });
        return svgNodes;
    });

    var layout = dagreD3.layout()
        .nodeSep(20)
        .rankSep(80)
        .rankDir("LR");

    var svg = d3.select("svg");
    var container = svg.select("g");
    var render = renderer.layout(layout).run(graph, container);

    svg.attr("viewBox", "0 0 " + (render.graph().width) + " " + (render.graph().height));

    container.call(d3.behavior.drag().on("drag", redraw));
    container.call(d3.behavior.zoom().on("zoom", redraw));
}

function getNodeValue(element, name) {
    var elementsByTagName = element.getElementsByTagName(name);
    if(elementsByTagName == null || elementsByTagName.length <= 0 ){
        return "";
    }
    var childNodes = elementsByTagName[0].childNodes;
    if(childNodes == null || childNodes.length <= 0){
        return "";
    }
    return childNodes[0].nodeValue;
}

function redraw() {
    for(var i = 0; i < Opentip.tips.length; i++) {
        if (Opentip.tips[i].visible) {
            Opentip.tips[i].reposition();
        }
    }
}

function toUpperCase(string) {
    return string.toUpperCase();
}
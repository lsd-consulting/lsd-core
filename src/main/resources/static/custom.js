// copy the element text to clipboard for the given element id
function copyToClipboard(id) {
    const copyText = document.getElementById(id).innerText;
    navigator.clipboard.writeText(copyText);
}

function addSliderForSvgZoom() {
    d3.selectAll("svg")
        .each(function () {
            let svg = d3.select(this);
            let svgParent = d3.select(this.parentNode);
            let viewBox = svg.attr("viewBox");
            let viewBoxDimensions = viewBox.split(" ");
            let width = viewBoxDimensions[2];
            let height = viewBoxDimensions[3];
            svgParent.insert('input', 'svg')
                .attr("min", 0.1)
                .attr("type", "range")
                .attr("max", 1)
                .attr("step", 0.01)
                .attr("value", 1)
                .on("input", function () {
                    let sliderValue = this.value;
                    let newWidth = width / sliderValue;
                    let newHeight = height / sliderValue;
                    let newViewBox = '0 0 ' + newWidth + ' ' + newHeight;
                    svg.attr("viewBox", newViewBox)
                });
        });
}

function highlightLifelinesWhenClicked() {
    d3.selectAll("svg")
        .each(function () {
            d3.select(this).selectAll('line')
                .filter(function () {
                    let currentLine = d3.select(this);
                    return currentLine.attr("x1") === currentLine.attr("x2"); // only vertical lifelines
                })
                .each(function () {
                    let currentLine = d3.select(this);
                    let originalStyle = currentLine.attr("style")
                    let clickedStyle = originalStyle + "stroke-width:4.0;"
                    let toggle = true
                    currentLine.on("click", function () {
                        let style = toggle ? clickedStyle : originalStyle
                        toggle = !toggle
                        currentLine.attr("style", style)
                    })
                })
        });
}

// WIP
function animateLines(){
    d3.selectAll("svg").each(function () {
        let currentSvg = d3.select(this);
        d3.select(this).selectAll('line')
            .filter(function () {
                let currentLine = d3.select(this);
                return currentLine.attr("y1") === currentLine.attr("y2"); // horizontal section of arrows
            })
            .each(function (p, j) {
                let currentLine = d3.select(this);
                let duration = 5000;
                let x1 = currentLine.attr("x1");
                let x2 = currentLine.attr("x2");
                let y1 = currentLine.attr("y1");
                currentSvg
                    .append("circle")
                    .attr("cx", x1)
                    .attr("cy", y1)
                    .attr('r','2px')
                    .transition()
                    .duration(duration)
                    .delay(j * duration)
                    .attr("cx", x2)
                    .remove()
            })
    });
}
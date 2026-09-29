// copy the element text to clipboard for the given element id
function copyToClipboard(id) {
    const copyText = document.getElementById(id).innerText;
    navigator.clipboard.writeText(copyText);
}

function addSliderForSvgZoom() {
    d3.selectAll(":has(> svg)")
        .each(function () {
            let figure = d3.select(this);
            figure.selectAll("svg")
                .each(function (value, index) {
                    let svg = d3.select(this)
                    let svgParent = d3.select(this.parentNode);
                    let viewBox = svg.attr("viewBox");
                    let viewBoxDimensions = viewBox.split(" ");
                    let originalWidth = viewBoxDimensions[2];
                    let originalHeight = viewBoxDimensions[3];
                    let i = ++index
                    
                    // Create container for zoom controls
                    let zoomContainer = svgParent.insert('div', 'svg:nth-of-type(' + i + ')')
                        .attr("class", "zoom-control");
                    
                    // Add magnifying glass icon
                    zoomContainer.append('span')
                        .attr("class", "zoom-label")
                        .html("&#128269;");
                    
                    // Add zoom out label (-)
                    zoomContainer.append('span')
                        .attr("class", "zoom-icon zoom-out")
                        .text("-");
                    
                    // Add slider
                    zoomContainer.append('input')
                        .attr("min", 0.0)
                        .attr("type", "range")
                        .attr("max", 1)
                        .attr("step", 0.01)
                        .attr("value", 1)
                        .attr("title", "Zoom control")
                        .on("input", adjustSvgZoom(originalWidth, originalHeight, svg));
                    
                    // Add zoom in label (+)
                    zoomContainer.append('span')
                        .attr("class", "zoom-icon zoom-in")
                        .text("+");
                })
        });
}

function adjustSvgZoom(originalWidth, originalHeight, svg) {
    return function () {
        let sliderValue = this.value;
        if (sliderValue < 0.1) {
            svg.attr("style", "display:none;");
        } else {
            let newWidth = originalWidth / sliderValue;
            let newHeight = originalHeight / sliderValue;
            let newViewBox = '0 0 ' + newWidth + ' ' + newHeight;
            svg.attr("viewBox", newViewBox)
            svg.attr("style", "display:block;") // also removes inline styling of height and width
            svg.attr("width", originalWidth * sliderValue)
            svg.attr("height", originalHeight * sliderValue)
        }
    };
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

const keywordOptions = {
    "element": "span",
    "className": "keyword"
};

// Add a span with class: "keyword" around matching keywords so that they can be styled
function highlightKeywords() {
    const patterns = [/Given/, /When/, /Then/, /And/];
    let keywordMarker = new Mark(document.querySelectorAll("section.description"));
    patterns.forEach(value => keywordMarker.markRegExp(value, keywordOptions));
}

const factOptions = {
    "element": "span",
    "className": "highlight",
    "separateWordSearch": false,
    "acrossElements": true,
    "accuracy": {
        "value": "exactly", "limiters": [",", ".", ";", ":", "-", "(", ")", "<", ">", "/"]
    }
}

function highlightFact(id, word) {
    let factMarker = new Mark(document.getElementById(id));
    factMarker.mark(word, factOptions);
}

<!-- Remove highlights in svg (causes text to disappear) -->
function unMarkSvg() {
    let unmarker = new Mark(document.querySelectorAll("svg"));
    unmarker.unmark();
}

<!-- show the svg (target='_self' explicitly added) hyperlink with the given id -->
function scrollIntoViewFor(id) {
    const target = document.querySelector("a[href='#" + id + "'][target='_self']");
    target.scrollIntoView();
    target.classList.add("highlight");
}

// Play logo video once forward, then in reverse, then stop
function playLogoVideo() {
    const video = document.querySelector('.logo-video');
    if (!video) return;

    video.currentTime = 0;
    video.playbackRate = 1.0;
    video.onended = null;
    video.play().catch(err => console.warn('Play failed:', err));

    video.onended = function forwardEnded() {
        video.onended = null; // Prevent re-trigger
        video.pause();

        let rafId = null;
        const frameDuration = 1 / 30; // 30 FPS → ~33.33ms per frame

        function reverseStep() {
            if (video.currentTime <= 0) {
                video.currentTime = 0;
                video.pause();
                cancelAnimationFrame(rafId);
                return;
            }
            // Step back by roughly one frame
            video.currentTime -= frameDuration;
            rafId = requestAnimationFrame(reverseStep);
        }
        // Start the reverse animation
        rafId = requestAnimationFrame(reverseStep);
    };
}

// Sticky toolbar: search + status chips for multi-scenario reports
function initScenarioToolbar() {
    const toolbar = document.querySelector('.report-toolbar');
    if (!toolbar) return;

    const search = toolbar.querySelector('.scenario-search');
    const chips = toolbar.querySelectorAll('.filter-chip');
    const scenarios = document.querySelectorAll('main details.scenario');
    const navLinks = toolbar.querySelectorAll('.scenario-nav-link');
    const activeStatuses = new Set(['success', 'warn', 'error']);

    function applyFilters() {
        const query = (search && search.value ? search.value : '').trim().toLowerCase();
        scenarios.forEach(function (details) {
            const status = details.getAttribute('data-status') || '';
            const title = (details.getAttribute('data-title') || '').toLowerCase();
            const statusOk = !status || activeStatuses.has(status);
            const searchOk = !query || title.indexOf(query) !== -1;
            details.hidden = !(statusOk && searchOk);
        });
        navLinks.forEach(function (link) {
            const status = link.getAttribute('data-status') || '';
            const title = (link.getAttribute('data-title') || link.textContent || '').toLowerCase();
            const statusOk = !status || activeStatuses.has(status);
            const searchOk = !query || title.indexOf(query) !== -1;
            link.hidden = !(statusOk && searchOk);
        });
    }

    if (search) {
        search.addEventListener('input', applyFilters);
    }

    chips.forEach(function (chip) {
        chip.addEventListener('click', function () {
            const status = chip.getAttribute('data-status');
            if (!status) return;
            const activeClass = 'active-' + status;
            if (activeStatuses.has(status)) {
                activeStatuses.delete(status);
                chip.classList.remove(activeClass);
                chip.setAttribute('aria-pressed', 'false');
            } else {
                activeStatuses.add(status);
                chip.classList.add(activeClass);
                chip.setAttribute('aria-pressed', 'true');
            }
            applyFilters();
        });
    });
}

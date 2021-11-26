let showSmoothCase = false;

function changeVisibilityOfSmoothCase() {
    showSmoothCase = !showSmoothCase;
    if (showSmoothCase) {
        document.getElementById("showSmoothCase").style["display"] = "none";
    } else {
        document.getElementById("showSmoothCase").style["display"] = "block";
    }
}
let showOutOfStock = false;

function changeVisibilityOfOutOfStock() {
    showOutOfStock = !showOutOfStock;
    if (showOutOfStock) {
        document.getElementById("showOutOfStock").style["display"] = "none";
    } else {
        document.getElementById("showOutOfStock").style["display"] = "block";
    }
}

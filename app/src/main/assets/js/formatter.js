function checkRGBA(id, displayer) {
    let re = /^#[0-9a-fA-F]{0,8}$/g;
    let value = document.getElementById(id).value;
    let padding = "FFFFFFFF";

    if (value != null && value.length > 0) {
        while (value.length > 0 && !re.test(value)) {
            value = value.substr(0, value.length - 1);
        }
        document.getElementById(id).setAttribute("value", value);
        document.getElementById(id).value = value;
        setColor(displayer, value + padding.substring(0, 9 - value.length));
    }

}

function checkFloat(id) {
    let re = /(^-|^-?(0|[1-9][0-9]*)(\.[0-9]*)?)$/g;
    let value = document.getElementById(id).value;
    if (value != null && value.length > 0) {
        while (value.length > 0 && !re.test(value)) {
            value = value.substr(0, value.length - 1);
        }
        document.getElementById(id).setAttribute("value", value);
        document.getElementById(id).value = value;
    }
}

function checkNonNegInteger(id) {
    let re = /(^0|^[1-9][0-9]*)$/g;
    let value = document.getElementById(id).value;
    if (value != null && value.length > 0) {
        while (value.length > 0 && !re.test(value)) {
            value = value.substr(0, value.length - 1);
        }
        document.getElementById(id).setAttribute("value", value);
        document.getElementById(id).value = value;
    }
}

function setColor(id, rgba) {
    let show = document.getElementById(id);
    show.setAttribute("style", "background: " + rgba);
}

function onChange(id) {
    let selector = document.getElementById(id);
    let index = selector.selectedIndex;
    let val = selector.options[index].value;

    let Width = document.getElementsByClassName('Width')[0];
    let Length = document.getElementsByClassName('Length')[0];
    let Height = document.getElementsByClassName('Height')[0];
    let Fraction = document.getElementsByClassName('Fraction')[0];
    let Edges = document.getElementsByClassName('Edges')[0];

    // alert(val);
    selector.setAttribute("choose", val);
    if (val.match(/^((Cube)|(Ball)|(Cone)|(Cylinder)|(Model))$/g)) {
        Width.style.display = "inline";
        Length.style.display = "inline";
        Height.style.display = "inline";
        Fraction.style.display = "none";
        Edges.style.display = "none";
    } else if (val.match(/^((Prism)|(Pyramid))$/g)) {
        Width.style.display = "inline";
        Length.style.display = "inline";
        Height.style.display = "inline";
        Fraction.style.display = "none";
        Edges.style.display = "inline";
    } else if (val.match(/^((Frustum))$/g)) {
        Width.style.display = "inline";
        Length.style.display = "inline";
        Height.style.display = "inline";
        Fraction.style.display = "inline";
        Edges.style.display = "inline";
    } else {
        alert("Not Match");
    }
}

function updateTexture(idx) {
    let selector = document.getElementById(idx);
    let index = selector.selectedIndex;
    let val = selector.options[index].value;

    selector.setAttribute("choose", val);

    let i;
    for (i = 0; i < selector.length; i++) {
        let img = selector.options[i].value;
        if (img == val) {
            document.getElementById("img_" + img).style.display = "inline";
        }
        else {
            document.getElementById("img_" + img).style.display = "none";
        }
    }
}

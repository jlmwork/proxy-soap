function responseTimeFieldFormatter(value, row) {
    if (value === -1) {
        return "Non disponible";
    }
    return value;
}
function statusFormatter(value) {
    if (value === "") {
        return "inconnu";
    } else if (value === "true") {
        return "valide";
    } else if (value === "false") {
        return "invalide";
    }
    return value;
}
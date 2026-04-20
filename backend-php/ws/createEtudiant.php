<?php
include_once '../service/EtudiantService.php';
include_once '../classes/Etudiant.php';

// Toujours définir le type de réponse JSON
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    // 🔹 Récupération + nettoyage des données
    $nom = trim($_POST['nom'] ?? '');
    $prenom = trim($_POST['prenom'] ?? '');
    $ville = trim($_POST['ville'] ?? '');
    $sexe = trim($_POST['sexe'] ?? '');

    // 🔹 Vérification des champs
    if (!empty($nom) && !empty($prenom) && !empty($ville) && !empty($sexe)) {

        // 🔹 Création objet (id = null car AUTO_INCREMENT)
        $etudiant = new Etudiant(null, $nom, $prenom, $ville, $sexe);

        $service = new EtudiantService();
        $service->create($etudiant);

        // 🔹 Retourner toute la liste mise à jour
        echo json_encode($service->findAllApi());

    } else {
        // 🔴 Réponse d'erreur claire
        echo json_encode([
            "status" => "error",
            "message" => "Tous les champs sont obligatoires"
        ]);
    }

} else {
    // 🔴 Si ce n'est pas une requête POST
    echo json_encode([
        "status" => "error",
        "message" => "Méthode non autorisée (POST uniquement)"
    ]);
}
?>
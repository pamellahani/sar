
### Étape 1 : Initialisation et Liaison
1. **Initialisation du `QueueBroker`** : Un thread serveur crée une instance de `QueueBroker` en lui donnant un nom spécifique.
2. **Liaison à un port** : Le serveur appelle la méthode `bind` du `QueueBroker`, spécifiant un port et un `AcceptListener`. Cette méthode est non bloquante et retourne immédiatement.

### Étape 2 : Connexion des Clients
1. **Initialisation de la Connexion** : Un client (ou un autre serveur) désirant établir une connexion avec le `QueueBroker` utilise la méthode `connect` :
   - `boolean connect(String name, int port, AcceptListener listener)`: Tente de connecter au `QueueBroker` en utilisant le `name` et `port` spécifiés, enregistrant un `AcceptListener` pour gérer les réponses de la connexion. Cette méthode est également non bloquante.
   - Si la tentative de connexion est lancée avec succès, `connect` retourne `true`.
   - En cas de réponse positive, l'`AcceptListener` du client est notifié via `accepted(MessageQueue queue)`, où `queue` est la `MessageQueue` attribuée à cette connexion.
   - Si la connexion est refusée, un `ConnectListener` peut être utilisé pour capturer cet événement à travers la méthode `refused`.

### Étape 3 : Gestion des Connexions Entrantes
1. **Notification de Connexions Acceptées** : Lorsqu'une connexion entrante est acceptée par le serveur, le `AcceptListener` configuré lors de la liaison (`bind`) est invoqué, recevant la `MessageQueue` associée à cette nouvelle connexion.

### Étape 4 : Communication Asynchrone
1. **Réception des Données** : Les `MessageQueue` des clients écoutent les messages entrants via des `Listener` configurés. Lorsque des données sont reçues, la méthode `received` est appelée.
2. **Envoi de Messages** : Les messages sont envoyés en utilisant les méthodes `send` de `MessageQueue`, qui sont non bloquantes.

### Étape 5 : Fermeture et Nettoyage
1. **Fermeture des Connexions** : Les `MessageQueue` peuvent être fermées par l'appel de `close`, après quoi `closed` peut être utilisée pour vérifier l'état de la queue.


# Découverte de motifs à la demande dans une base de données distribuée

**Sammury**

Only few pattern mining methods are dedicated to distributed databases. In fact, the centralization of data is often less expensive than the communication of all mined patterns. To circumvent this difficulty, this paper follows a parsimonious approach by sampling patterns. We propose the algorithm DDSampling that draws a pattern from a distributed database proportionally to its interest. We demonstrate its accuracy and analyze its complexity. Experiments show on several datasets its robustness against the failures of a site or the network.
****************************************************************************************

**Résumé**

De nombreuses applications s’appuient sur des bases de données distribuées. Pourtant, peu de méthodes de découverte de motifs ont été proposées pour les extraire sans centraliser les données. Il faut dire que cette centralisation est souvent moins coûteuse que la communication des motifs extraits. Pour contourner cette difficulté, cet article adopte une approche parcimonieuse en coûts de communication en fournissant à l’utilisateur des motifs à la demande. Plus précisément, nous proposons l’algorithme DDSAMPLING qui tire un motif dans une base de données distribuée proportionnellement à son intérêt. Nous démontrons son exactitude et analysons sa complexité en temps et en communication soulignant son efficacité. Enfin, une étude expérimentale montre sur plusieurs jeux de données la robustesse de DDSampling face aux défaillances d’un site ou du réseau.
****************************************************************************************


**DDSampling (Distributed Database Sampling)**

![Alt text](https://github.com/DDSampling/DDSampling/blob/master/Dataset%20no%20splited/DDSampling.PNG?raw=true "Title")

**Robustesse de DDSampling**

![Alt text](https://github.com/DDSampling/DDSampling/blob/master/Dataset%20no%20splited/TRejet1.PNG?raw=true "Title")

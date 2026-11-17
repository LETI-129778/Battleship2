# Oportunidades de Refabricação

| Local (Classe ou Classe::método) | Nome do cheiro no código (Code Smell) | Nome da refabricação | Número de aluno |
| :--- | :--- | :--- | :--- |
| `Fleet` | Duplicated Code (strings "caravela") | Introduce Constant | 129778 |
| `Fleet` | Duplicated Code (strings "barca") | Introduce Constant | 129778 |
| `Game::RandomEnemyFire` | Long Method / High Complexity | Extract Method | 129774 |
| `IGameTest` | Dead Code / Redundancy | Safe Delete | 129774 |
| `Move` | Duplicated Code | Introduce Constant | 129778 |
| `Move::processEnemyFire` | Long Method / High Complexity | Extract Method | 129778 |
| `Ship` | Primitive Obsession / Long Parameter List | Replace Type with Object | 129774 |
| `Tasks` | Duplicated Code | Introduce Constant | 129773 |
| `Tasks::menu` | Long Method / High Complexity | Extract Method | 129773 |
| `Tasks::atualizarInterfaceGrafica` | Long Method / High Complexity | Extract Method | 129773 |
| `TasksTest::testAtualizarInterfaceGraficaHitAndMiss` | Long Method / High Complexity | Extract Method | 129773 |
| `Move::processEnemyFire` | Data Clumps | Extract Class (ShotStats) | 129778 |
| `Move` | Duplicated Code | Extract Method | 129778 |
| `Fleet::getShipsLike, Fleet::getFloatingShips, Fleet::getSunkShips` | Duplicated Code (mesmo ciclo de filtragem) | Extract Method | 129778 |
| `Fleet::colisionRisk` | Loop com índice e retorno antecipado | Replace Loop with Pipeline | 129778 |
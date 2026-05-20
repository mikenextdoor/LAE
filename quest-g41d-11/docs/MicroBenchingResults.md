# Resultados do Microbenchmark JMH

## Configuração

- **Framework:** JMH (Java Microbenchmark Harness)
- **Modo:** Average Time (`avgt`)
- **Unidade:** Nanosegundos por operação (`ns/op`)
- **Iterações de warmup:** 4 × 2s
- **Iterações de medição:** 8 × 2s
- **JVM:** Java 23

## Resultados

| Benchmark | Modo | Iterações | Score (ns/op) | Erro (±) |
|---|---|---|---|---|
| `Bench.queryJDBC` | avgt | 30 | 625,538 | ± 30,159 |
| `Bench.queryReflection` | avgt | 30 | 610,331 | ± 12,048 |
| `Bench.queryDynamic` | avgt | 30 | 588,961 | ±  9,856 |

## Análise e Conclusão

Os resultados mostram que a implementação baseada em Dynamic Reflection já esta mais rapida que a implementação ad hoc em JDBC, e com um overhead menor que na versao de Reflcetion.

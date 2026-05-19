# Resultados do Microbenchmark JMH

## Configuração

- **Framework:** JMH (Java Microbenchmark Harness)
- **Modo:** Average Time (`avgt`)
- **Unidade:** Nanosegundos por operação (`ns/op`)
- **Iterações de warmup:** 4 × 2s
- **Iterações de medição:** 4 × 2s
- **JVM:** Java 23

## Resultados

| Benchmark | Modo | Iterações | Score (ns/op) | Erro (±) |
|---|---|---|---|---|
| `Bench.queryJDBC` | avgt | 4 | 1630,819 | ± 450,459 |
| `Bench.queryReflection` | avgt | 4 | 2110,375 | ± 2595,351 |

## Análise e Conclusão

Os resultados mostram que a implementação baseada em Reflection continua mais lenta do que a implementação ad hoc em JDBC, mas com um overhead significativamente menor do que na versão inicial.

Para operações de query sobre a entidade `Interacao`, a implementação com Reflection apresentou um slowdown aproximado de **1.29x (~29%)** relativamente à implementação JDBC.

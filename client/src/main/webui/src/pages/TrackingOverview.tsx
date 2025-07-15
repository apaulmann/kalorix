import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import { ChevronDown, ChevronUp } from "lucide-react";

interface TrackingEntry {
    id?: number;
    zeitstempel: string;
    kcal: number;
    beschreibung: string;
}

interface DayGroup {
    date: string;
    entries: TrackingEntry[];
    summe: number;
    differenz: number | null;
}

interface Props {
    open: boolean;
    onClose: () => void;
    token: string;
    user: string;
    tagesbedarf: number | null;
}

export function TrackingOverview({ open, onClose, token, user, tagesbedarf }: Props) {
    const [entries, setEntries] = useState<TrackingEntry[]>([]);
    const [expanded, setExpanded] = useState<Set<string>>(new Set());

    useEffect(() => {
        if (!open) return;

        fetch(`http://localhost:8081/api/kaltracking/all/${user}`, {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then(res => res.ok ? res.json() : [])
            .then(setEntries);
    }, [open, user, token]);

    const grouped = entries.reduce((acc: Record<string, TrackingEntry[]>, entry) => {
        const date = new Date(entry.zeitstempel).toLocaleDateString("de-DE");
        acc[date] = acc[date] || [];
        acc[date].push(entry);
        return acc;
    }, {});

    const toggleDay = (day: string) => {
        const newSet = new Set(expanded);
        if (newSet.has(day)) newSet.delete(day);
        else newSet.add(day);
        setExpanded(newSet);
    };

    const groupedArray: DayGroup[] = Object.entries(grouped)
        .map(([date, entries]) => {
            const summe = entries.reduce((acc, e) => acc + e.kcal, 0);
            const differenz = tagesbedarf !== null ? tagesbedarf - summe : null;
            return { date, entries, summe, differenz };
        })
        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent className="max-h-[99vh] overflow-y-auto p-0">
                <DialogHeader>
                    <DialogTitle>Tagesübersicht</DialogTitle>
                </DialogHeader>
                {groupedArray.map((day) => (
                    <div key={day.date} className="border-b pb-1 mb-1">
                        <div
                            className="flex justify-between items-center cursor-pointer"
                            onClick={() => toggleDay(day.date)}
                        >
                            <div>
                                <div className="font-semibold">{day.date}</div>
                                <div className="text-sm text-gray-400">
                                    Summe: {day.summe} kcal –{" "}
                                    Differenz: <span className={day.differenz! >= 0 ? "text-green-400" : "text-red-400"}>
                                        {day.differenz! >= 0 ? "+" : ""}{day.differenz} kcal
                                    </span>
                                </div>
                            </div>
                            {expanded.has(day.date) ? <ChevronUp /> : <ChevronDown />}
                        </div>
                        {expanded.has(day.date) && (
                            <div className="mt-2 space-y-2">
                                {day.entries.map((e) => (
                                    <div key={e.id} className="flex justify-between text-sm">
                                        <div className="text-left">
                                            <div className="text-gray-400">{new Date(e.zeitstempel).toLocaleTimeString()}</div>
                                            <div>{e.beschreibung}</div>
                                        </div>
                                        <div className="text-right font-semibold">{e.kcal} kcal</div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                ))}
                <div className="mt-4 text-center">
                    <Button onClick={onClose} variant="secondary">Schließen</Button>
                </div>
            </DialogContent>
        </Dialog>
    );
}

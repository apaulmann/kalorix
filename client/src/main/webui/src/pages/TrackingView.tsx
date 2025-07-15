import {useEffect, useState} from "react";
import {Button} from "@/components/ui/button";
import {Card, CardContent, CardHeader} from "@/components/ui/card";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Textarea} from "@/components/ui/textarea";
import {toast} from "sonner";
import {UserDetailsDialog} from "@/pages/UserDetailsDialog";
import {Trash2} from "lucide-react";
import {SiOpenai} from "react-icons/si";
import {TbLogout} from "react-icons/tb";
import {IoSettingsOutline} from "react-icons/io5";
import {BsListColumns} from "react-icons/bs";
import {TrackingOverview} from "@/pages/TrackingOverview";

interface TrackingEntry {
    id?: number;
    zeitstempel: string;
    kcal: number;
    beschreibung: string;
}

interface UserDetails {
    kcalVerbrauch: number;
}

interface TrackingViewProps {
    onLogout: () => void;
    user: string;
    token: string;
}

export function TrackingView({onLogout, user, token}: TrackingViewProps) {
    const [showDetailsDialog, setShowDetailsDialog] = useState(false);
    const [entries, setEntries] = useState<TrackingEntry[]>([]);
    const [kcal, setKcal] = useState<number>(0);
    const [beschreibung, setBeschreibung] = useState("");
    const [tagesbedarf, setTagesbedarf] = useState<number | null>(null);
    const [showOverview, setShowOverview] = useState(false);

    const apiTrackingUrl = `http://localhost:8081/api/kaltracking`;
    const apiDetailsUrl = `http://localhost:8081/api/user-details`;

    useEffect(() => {
        fetch(`${apiTrackingUrl}/today/${user}`, {
            headers: {Authorization: `Bearer ${token}`},
        })
            .then((res) => res.ok ? res.json() : [])
            .then((data) => setEntries(data));
    }, [user, token]);

    useEffect(() => {
        fetch(`${apiDetailsUrl}/${user}`, {
            headers: {Authorization: `Bearer ${token}`},
        })
            .then((res) => res.ok ? res.json() : null)
            .then((data: UserDetails | null) => {
                if (data?.kcalVerbrauch) {
                    setTagesbedarf(data.kcalVerbrauch);
                }
            });
    }, [user, token]);

    const onAskGPT = async () => {
        const food = beschreibung.trim();
        if (!food) return;

        try {
            const res = await fetch(`http://localhost:8081/api/chatty/${encodeURIComponent(food)}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (res.ok) {
                const kcalWert = await res.json();
                if (typeof kcalWert === "number") {
                    setKcal(kcalWert);
                    toast.success(`ChatGPT schätzt: ${kcalWert} kcal`);
                } else {
                    toast.error("Unerwartete Antwort vom Server");
                }
            } else {
                toast.error("Fehler bei der Anfrage an ChatGPT");
            }
        } catch (e) {
            toast.error("Netzwerkfehler bei GPT-Anfrage");
        }
    };

    const handleSave = async () => {
        if (kcal <= 0) {
            toast.error("Bitte gültige kcal eingeben.");
            return;
        }

        const newEntry: TrackingEntry = {
            zeitstempel: new Date().toISOString(),
            kcal,
            beschreibung,
        };

        const res = await fetch(`${apiTrackingUrl}/${user}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(newEntry),
        });

        if (res.ok) {
            const created = await res.json();
            setEntries((prev) => [created, ...prev]);
            setKcal(0);
            setBeschreibung("");
            toast.success("Eintrag gespeichert");
        } else {
            toast.error("Fehler beim Speichern");
        }
    };

    const handleDelete = async (ent: TrackingEntry | undefined) => {
        if (!ent) return;
        const res = await fetch(`${apiTrackingUrl}/${user}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(ent),
        });
        if (res.ok) {
            setEntries((prev) => prev.filter((e) => e.id !== ent.id));
            toast.success("Eintrag gelöscht");
        } else {
            toast.error("Fehler beim Löschen");
        }
    };

    const summe = entries.reduce((acc, e) => acc + e.kcal, 0);
    const differenz = tagesbedarf !== null ? tagesbedarf - summe : null;

    return (
        <div className="min-h-screen flex flex-col gap-1 items-center bg-gray-950 text-white px-0 py-0">
            <UserDetailsDialog
                open={showDetailsDialog}
                onClose={() => setShowDetailsDialog(false)}
                person={user}
                token={token}
            />
            <TrackingOverview
                open={showOverview}
                onClose={() => setShowOverview(false)}
                user={user}
                token={token}
                tagesbedarf={tagesbedarf}
            />

            <Card className="w-full max-w-2xl py-1 px-0 ">
                <CardHeader>
                    <div className="flex items-center justify-between flex-wrap gap-1 mt-2">
                        {tagesbedarf !== null && (
                            <div className="text-sm text-gray-400">
                                Tagesbedarf: <b>{tagesbedarf} kcal</b>
                            </div>
                        )}
                        <div className="flex gap-2">
                            <Button variant="secondary" onClick={() => setShowDetailsDialog(true)}>
                                <IoSettingsOutline className="w-5 h-5"/>
                            </Button>
                            <Button variant="outline" onClick={() => setShowOverview(true)}>
                                <BsListColumns className="w-5 h-5"/>
                            </Button>
                            <Button variant="outline" onClick={onLogout}>
                                <TbLogout className="w-5 h-5"/>
                            </Button>
                        </div>
                    </div>

                </CardHeader>

                <CardContent className="space-y-1">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                        <div>
                            <Label htmlFor="beschreibung">Beschreibung</Label>
                            <Textarea
                                id="beschreibung"
                                value={beschreibung}
                                onChange={(e) => setBeschreibung(e.target.value)}
                                placeholder="z. B. Joghurt mit Beeren"
                            />
                        </div>
                        <div>
                            <Label htmlFor="kcal">kcal</Label>
                            <div className="flex gap-2">
                                <Input
                                    id="kcal"
                                    type="number"
                                    value={kcal}
                                    onChange={(e) => setKcal(+e.target.value)}
                                    placeholder="z. B. 550"
                                />
                                <Button
                                    variant="ghost"
                                    onClick={onAskGPT}
                                    disabled={!beschreibung.trim()}
                                    title="Kalorien schätzen"
                                >
                                    <SiOpenai className="w-5 h-5"/>
                                </Button>
                            </div>
                        </div>
                    </div>
                    <Button onClick={handleSave} className="w-full">
                        Neuer Eintrag
                    </Button>
                </CardContent>
            </Card>

            {entries.length > 0 && (
                <div className="w-full max-w-2xl mt-4">
                    <Card className="w-full max-w-2xl">
                        <CardContent className="space-y-3">
                            {entries.map((entry) => (
                                <div
                                    key={entry.id}
                                    className="border-b border-gray-700 pb-1 flex flex-col flex-row justify-between items-start md:items-center"
                                >
                                    <div className="flex flex-col text-left">
                                        <div className="text-sm text-gray-400">
                                            {new Date(entry.zeitstempel).toLocaleString()}
                                        </div>
                                        <div className="text-base">{entry.beschreibung}</div>
                                    </div>

                                    <div
                                        className="flex items-center gap-4 mt-2 md:mt-0 md:justify-end whitespace-nowrap">
                                        <div className="font-bold text-right">{entry.kcal} kcal</div>
                                        <Button variant="ghost" size="icon" onClick={() => handleDelete(entry)}>
                                            <Trash2 className="w-4 h-4 text-red-400"/>
                                        </Button>
                                    </div>
                                </div>
                            ))}

                        </CardContent>
                    </Card>
                    <div className="sticky bottom-0 bg-gray-950 border-t border-gray-800 px-4 py-2 z-10">
                        <div className="text-right text-sm text-gray-300">
                            <div>Summe: <b>{summe} kcal</b></div>
                            {differenz !== null && (
                                <div>
                                    Differenz:{" "}
                                    <b className={differenz >= 0 ? "text-green-400" : "text-red-400"}>
                                        {differenz > 0 ? "+" : ""}
                                        {differenz} kcal
                                    </b>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
